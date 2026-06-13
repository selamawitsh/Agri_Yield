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

    // ── KEY FIX: resolve merchant profile ID from either userId OR profileId ──
    // The voucher-service passes the JWT userId (e.g. 89e7c5df) as merchantId.
    // Products are stored under the merchant profile ID (e.g. 7affb087).
    // This method handles both cases so gRPC calls always find the right products.
    private UUID resolveProfileId(UUID merchantIdOrUserId) {
        // Try direct profile lookup first (fast path — called from REST endpoints)
        Optional<MerchantProfile> byId = merchantProfileRepository.findById(merchantIdOrUserId);
        if (byId.isPresent()) return byId.get().getId();

        // Fall back to userId lookup (gRPC path — voucher-service passes userId)
        Optional<MerchantProfile> byUserId =
            merchantProfileRepository.findByUserId(merchantIdOrUserId);
        if (byUserId.isPresent()) return byUserId.get().getId();

        throw new MerchantNotFoundException("Merchant not found: " + merchantIdOrUserId);
    }

    @Override
    @Transactional
    public MerchantProfile registerMerchant(UUID userId, RegisterMerchantRequest request) {
        if (merchantProfileRepository.findByUserId(userId).isPresent()) {
            throw new BusinessException("Merchant profile already exists for this user");
        }
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
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "merchant.registration.requested");
        event.put("user_id", userId.toString());
        event.put("business_name", request.getBusinessName());
        event.put("timestamp", OffsetDateTime.now().toString());
        eventPublisher.publish("user.exchange", "merchant.registration.requested", event);
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
        // Try by profile ID first, then by user ID
        return merchantProfileRepository.findById(merchantId)
            .or(() -> merchantProfileRepository.findByUserId(merchantId))
            .orElseThrow(() -> new MerchantNotFoundException("Merchant not found: " + merchantId));
    }

    @Override
    @Transactional
    public MerchantProfile updateMerchantProfile(UUID userId, UpdateMerchantRequest request) {
        MerchantProfile existing = getMerchantProfile(userId);
        if (request.getBusinessName()    != null) existing.setBusinessName(request.getBusinessName());
        if (request.getStoreGpsLat()     != null) existing.setStoreGpsLat(request.getStoreGpsLat());
        if (request.getStoreGpsLng()     != null) existing.setStoreGpsLng(request.getStoreGpsLng());
        if (request.getTelebirrAccount() != null) existing.setTelebirrAccount(request.getTelebirrAccount());
        existing.setUpdatedAt(OffsetDateTime.now());
        return merchantProfileRepository.save(existing);
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
            .quantityInStock(request.getQuantityInStock() != null
                ? request.getQuantityInStock() : BigDecimal.ZERO)
            .unitOfMeasure(request.getUnit())
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
        if (!product.getMerchantId().equals(merchant.getId()))
            throw new BusinessException("Product does not belong to this merchant");
        if (request.getCurrentPriceEtb() != null &&
                request.getCurrentPriceEtb().compareTo(product.getCurrentPriceEtb()) != 0) {
            priceHistoryRepository.save(PriceHistory.builder()
                .productId(productId).oldPriceEtb(product.getCurrentPriceEtb())
                .newPriceEtb(request.getCurrentPriceEtb())
                .changedAt(OffsetDateTime.now()).changedBy(userId).build());
            product.setCurrentPriceEtb(request.getCurrentPriceEtb());
        }
        if (request.getProductName()     != null) product.setProductName(request.getProductName());
        if (request.getIsAvailable()     != null) product.setAvailable(request.getIsAvailable());
        if (request.getQuantityInStock() != null) {
            product.setQuantityInStock(request.getQuantityInStock());
            product.setAvailable(request.getQuantityInStock().compareTo(BigDecimal.ZERO) > 0);
        }
        product.setUpdatedAt(OffsetDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsByMerchant(UUID merchantId) {
        UUID profileId = resolveProfileId(merchantId);
        return productRepository.findByMerchantId(profileId);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID userId, UUID productId) {
        MerchantProfile merchant = getMerchantProfile(userId);
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
        if (!product.getMerchantId().equals(merchant.getId()))
            throw new BusinessException("Product does not belong to this merchant");
        productRepository.deleteById(productId);
    }

    @Override
    public List<Product> getMerchantInventory(UUID userId) {
        MerchantProfile merchant = getMerchantProfile(userId);
        return productRepository.findByMerchantId(merchant.getId());
    }

    @Override
    public List<PriceAnomaly> getPriceAnomalies(UUID merchantId) {
        UUID profileId = resolveProfileId(merchantId);
        return priceAnomalyRepository.findByMerchantId(profileId);
    }

    @Override
    public List<String> getMerchantCategories(UUID merchantId) {
        // FIXED: resolve profile ID — merchantId from gRPC is userId
        UUID profileId = resolveProfileId(merchantId);
        return productRepository.findByMerchantId(profileId).stream()
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
        return merchantProfileRepository.findById(merchantId).isPresent()
            || merchantProfileRepository.findByUserId(merchantId).isPresent();
    }

    @Override
    public double getRegionalPriceIndex(String kebeleCode, String category) {
        return priceIndexCache.getRegionalMedian(kebeleCode, category)
            .map(BigDecimal::doubleValue).orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public Product checkInventoryForRedemption(UUID merchantId, String category,
                                               BigDecimal requiredQty) {
        // FIXED: resolve profile ID from userId
        UUID profileId = resolveProfileId(merchantId);
        log.info("checkInventory: userId={} → profileId={} category={} qty={}",
            merchantId, profileId, category, requiredQty);

        List<Product> products = productRepository.findByMerchantId(profileId).stream()
            .filter(p -> category.equals("OTHER") ||
                p.getProductCategory().name().equals(category))
            .collect(Collectors.toList());

        if (products.isEmpty()) {
            throw new BusinessException(
                "Merchant does not carry category: " + category +
                ". Add this product type to your inventory first.",
                "PRODUCT_NOT_AVAILABLE");
        }

        return products.stream()
            .filter(p -> p.hasSufficientStock(requiredQty))
            .findFirst()
            .orElseThrow(() -> {
                Product any = products.get(0);
                BigDecimal available = any.getQuantityInStock() != null
                    ? any.getQuantityInStock() : BigDecimal.ZERO;
                return new BusinessException(
                    "Insufficient stock. Required: " + requiredQty + " " + any.getUnit() +
                    ", Available: " + available + " " + any.getUnit(),
                    "INSUFFICIENT_STOCK");
            });
    }

    @Override
    @Transactional
    public void deductInventory(UUID merchantId, String category, BigDecimal quantity) {
        // FIXED: resolve profile ID from userId
        UUID profileId = resolveProfileId(merchantId);
        productRepository.findByMerchantId(profileId).stream()
            .filter(p -> category.equals("OTHER") ||
                p.getProductCategory().name().equals(category))
            .filter(p -> p.hasSufficientStock(quantity))
            .findFirst()
            .ifPresentOrElse(product -> {
                product.deductStock(quantity);
                product.setUpdatedAt(OffsetDateTime.now());
                productRepository.save(product);
                log.info("Inventory deducted: {} {} of {} — merchant profile: {}",
                    quantity, product.getUnit(), product.getProductName(), profileId);
            }, () -> log.warn(
                "DeductInventory: no matching product. profileId={} category={} qty={}",
                profileId, category, quantity));
    }

    public List<Product> getProductsByCategoryAndKebele(String category, String kebeleCode) {
        List<UUID> merchantIds = userServiceClient.getMerchantIdsByKebele(kebeleCode);
        if (merchantIds.isEmpty()) return List.of();
        return productRepository.findByMerchantIdsAndCategory(merchantIds, category);
    }
}
