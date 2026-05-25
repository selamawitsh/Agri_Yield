package com.agriyield.merchantservice.application.port.incoming;

import com.agriyield.merchantservice.domain.model.MerchantProfile;
import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import com.agriyield.merchantservice.domain.model.Product;
import com.agriyield.merchantservice.presentation.dto.request.RegisterMerchantRequest;
import com.agriyield.merchantservice.presentation.dto.request.UpdateMerchantRequest;
import com.agriyield.merchantservice.presentation.dto.request.CreateProductRequest;
import com.agriyield.merchantservice.presentation.dto.request.UpdateProductRequest;

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
}
