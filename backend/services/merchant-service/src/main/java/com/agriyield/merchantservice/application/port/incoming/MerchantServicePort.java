package com.agriyield.merchantservice.application.port.incoming;

import com.agriyield.merchantservice.domain.model.MerchantProfile;
import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import com.agriyield.merchantservice.domain.model.Product;
import com.agriyield.merchantservice.presentation.dto.request.RegisterMerchantRequest;
import com.agriyield.merchantservice.presentation.dto.request.UpdateMerchantRequest;
import com.agriyield.merchantservice.presentation.dto.request.CreateProductRequest;
import com.agriyield.merchantservice.presentation.dto.request.UpdateProductRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface MerchantServicePort {

    MerchantProfile registerMerchant(UUID userId, RegisterMerchantRequest request);
    MerchantProfile getMerchantProfile(UUID userId);
    MerchantProfile getMerchantById(UUID merchantId);
    MerchantProfile updateMerchantProfile(UUID userId, UpdateMerchantRequest request);
    Product createProduct(UUID userId, CreateProductRequest request);
    Product updateProduct(UUID userId, UUID productId, UpdateProductRequest request);
    List<Product> getProductsByMerchant(UUID merchantId);
    void deleteProduct(UUID userId, UUID productId);
    List<Product> getMerchantInventory(UUID userId);
    List<PriceAnomaly> getPriceAnomalies(UUID merchantId);

    // gRPC-facing methods
    List<String> getMerchantCategories(UUID merchantId);
    double[] getMerchantLocation(UUID merchantId);
    boolean isMerchantActive(UUID merchantId);
    double getRegionalPriceIndex(String kebeleCode, String category);

    /**
     * Inventory check before voucher redemption (SRS §3.5.3 Check #3 extension).
     * Returns the matching product, or throws BusinessException if:
     *   - merchant has no product in this category → PRODUCT_NOT_AVAILABLE
     *   - merchant has the product but insufficient quantity → INSUFFICIENT_STOCK
     *
     * @param merchantId   the merchant processing the voucher
     * @param category     voucher product category (SEED, FERTILIZER, etc.)
     * @param requiredQty  quantity the voucher covers (derived from amountEtb / price)
     * @return the matching Product with sufficient stock
     */
    Product checkInventoryForRedemption(UUID merchantId, String category,
                                        BigDecimal requiredQty);

    /**
     * Deducts stock after a successful voucher redemption.
     * Called only after all 6 checks pass and escrow is released.
     *
     * @param merchantId  the merchant
     * @param category    voucher product category
     * @param quantity    amount to deduct
     */
    void deductInventory(UUID merchantId, String category, BigDecimal quantity);
}
