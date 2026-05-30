package com.agriyield.merchantservice.application.service;

import com.agriyield.merchantservice.application.port.incoming.MerchantServicePort;
import com.agriyield.merchantservice.application.port.outgoing.*;
import com.agriyield.merchantservice.domain.enums.ProductCategory;
import com.agriyield.merchantservice.domain.enums.SubscriptionTier;
import com.agriyield.merchantservice.domain.exception.BusinessException;
import com.agriyield.merchantservice.domain.exception.MerchantNotFoundException;
import com.agriyield.merchantservice.domain.exception.ProductNotFoundException;
import com.agriyield.merchantservice.domain.model.MerchantProfile;
import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import com.agriyield.merchantservice.domain.model.PriceHistory;
import com.agriyield.merchantservice.domain.model.Product;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.grpc.UserServiceClient;
import com.agriyield.merchantservice.presentation.dto.request.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantServicePort {

    private final MerchantProfileRepositoryPort merchantProfileRepository;
    private final ProductRepositoryPort productRepository;
    private final PriceHistoryRepositoryPort priceHistoryRepository;
    private final PriceAnomalyRepositoryPort priceAnomalyRepository;
    private final EventPublisherPort eventPublisher;
    private final PriceIndexCachePort priceIndexCache;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public MerchantProfile registerMerchant(UUID userId, RegisterMerchantRequest request) {

        if (merchantProfileRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("Merchant profile already exists for this user");
        }

        // Save locally so all other operations work immediately
        MerchantProfile profile = MerchantProfile.builder()
                .userId(userId)
                .businessName(request.getBusinessName())
                .businessLicenseNumber(request.getBusinessLicenseNumber())
                .storeGpsLat(request.getStoreGpsLat())
                .storeGpsLng(request.getStoreGpsLng())
                .telebirrAccount(request.getTelebirrAccount())
                .subscriptionTier(SubscriptionTier.BASIC)
                .isPhysicallyVerified(false)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        MerchantProfile saved = merchantProfileRepository.save(profile);

        // Also publish event so user-service stays in sync
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "merchant.registration.requested");
        event.put("user_id", userId.toString());
        event.put("business_name", request.getBusinessName());
        event.put("business_license_number", request.getBusinessLicenseNumber());
        event.put("store_gps_lat", request.getStoreGpsLat());
        event.put("store_gps_lng", request.getStoreGpsLng());
        event.put("telebirr_account", request.getTelebirrAccount());
        event.put("kebele_code", request.getKebeleCode());
        event.put("timestamp", OffsetDateTime.now().toString());

        eventPublisher.publish("user.exchange", "merchant.registration.requested", event);
        log.info("Merchant registered locally and event published for userId={}", userId);

        return saved;
    }

    @Override
    public MerchantProfile getMerchantProfile(UUID userId) {
        return merchantProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new MerchantNotFoundException(
                        "Merchant profile not found for user: " + userId));
    }

    @Override
    public MerchantProfile getMerchantById(UUID merchantId) {
        return merchantProfileRepository.findById(merchantId)
                .orElseThrow(() -> new MerchantNotFoundException(
                        "Merchant not found: " + merchantId));
    }

    @Override
    @Transactional
    public MerchantProfile updateMerchantProfile(UUID userId, UpdateMerchantRequest request) {

        MerchantProfile existing = getMerchantProfile(userId);

        if (request.getBusinessName() != null) {
            existing.setBusinessName(request.getBusinessName());
        }
        if (request.getStoreGpsLat() != null) {
            existing.setStoreGpsLat(request.getStoreGpsLat());
        }
        if (request.getStoreGpsLng() != null) {
            existing.setStoreGpsLng(request.getStoreGpsLng());
        }
        if (request.getTelebirrAccount() != null) {
            existing.setTelebirrAccount(request.getTelebirrAccount());
        }
        existing.setUpdatedAt(OffsetDateTime.now());

        MerchantProfile updated = merchantProfileRepository.save(existing);

        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "merchant.profile.update.requested");
        event.put("user_id", userId.toString());
        event.put("timestamp", OffsetDateTime.now().toString());
        eventPublisher.publish("user.exchange", "merchant.profile.update.requested", event);

        return updated;
    }

    @Override
    @Transactional
    public Product createProduct(UUID userId, CreateProductRequest request) {

        MerchantProfile merchant = getMerchantProfile(userId);

        Product product = Product.builder()
                .merchantId(merchant.getId())
                .productName(request.getProductName())
                .productCategory(ProductCategory.valueOf(request.getProductCategory()))
                .unit(request.getUnit())
                .currentPriceEtb(request.getCurrentPriceEtb())
                .isAvailable(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(UUID userId, UUID productId, UpdateProductRequest request) {

        MerchantProfile merchant = getMerchantProfile(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        if (!product.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException("Product does not belong to this merchant");
        }

        if (request.getCurrentPriceEtb() != null &&
                request.getCurrentPriceEtb().compareTo(product.getCurrentPriceEtb()) != 0) {

            PriceHistory history = PriceHistory.builder()
                    .productId(productId)
                    .oldPriceEtb(product.getCurrentPriceEtb())
                    .newPriceEtb(request.getCurrentPriceEtb())
                    .changedAt(OffsetDateTime.now())
                    .changedBy(userId)
                    .build();

            priceHistoryRepository.save(history);
            product.setCurrentPriceEtb(request.getCurrentPriceEtb());
        }

        if (request.getProductName() != null) product.setProductName(request.getProductName());
        if (request.getIsAvailable() != null) product.setAvailable(request.getIsAvailable());

        product.setUpdatedAt(OffsetDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByMerchant(UUID merchantId) {
        return productRepository.findByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID userId, UUID productId) {

        MerchantProfile merchant = getMerchantProfile(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        if (!product.getMerchantId().equals(merchant.getId())) {
            throw new BusinessException("Product does not belong to this merchant");
        }

        productRepository.deleteById(productId);
    }

    @Override
    public List<Product> getMerchantInventory(UUID userId) {
        MerchantProfile merchant = getMerchantProfile(userId);
        return productRepository.findByMerchantId(merchant.getId());
    }

    @Override
    public List<PriceAnomaly> getPriceAnomalies(UUID merchantId) {
        return priceAnomalyRepository.findByMerchantId(merchantId);
    }

    @Override
    public List<String> getMerchantCategories(UUID merchantId) {
        return productRepository.findByMerchantId(merchantId)
                .stream()
                .filter(Product::isAvailable)
                .map(p -> p.getProductCategory().name())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public double[] getMerchantLocation(UUID merchantId) {
        MerchantProfile merchant = getMerchantById(merchantId);
        return new double[]{merchant.getStoreGpsLat(), merchant.getStoreGpsLng()};
    }

    @Override
    public boolean isMerchantActive(UUID merchantId) {
        return merchantProfileRepository.findById(merchantId).isPresent();
    }

    @Override
    public double getRegionalPriceIndex(String kebeleCode, String category) {
        return priceIndexCache.getRegionalMedian(kebeleCode, category)
                .map(BigDecimal::doubleValue)
                .orElse(0.0);
    }

    public List<Product> getProductsByCategoryAndKebele(String category, String kebeleCode) {
        List<UUID> merchantIds = userServiceClient.getMerchantIdsByKebele(kebeleCode);
        if (merchantIds.isEmpty()) return List.of();
        return productRepository.findByMerchantIdsAndCategory(merchantIds, category);
    }
}
