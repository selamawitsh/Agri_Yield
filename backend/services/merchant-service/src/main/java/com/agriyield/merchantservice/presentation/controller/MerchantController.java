package com.agriyield.merchantservice.presentation.controller;

import com.agriyield.merchantservice.application.port.incoming.MerchantServicePort;
import com.agriyield.merchantservice.domain.model.MerchantProfile;
import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import com.agriyield.merchantservice.domain.model.Product;
import com.agriyield.merchantservice.presentation.dto.request.*;
import com.agriyield.merchantservice.presentation.dto.response.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantServicePort merchantService;

    // ── MS-01: Register Merchant ─────────────────────────────────────────────
    @PostMapping("/api/v1/merchants/register")
    public ResponseEntity<ApiResponse<MerchantProfileResponse>> registerMerchant(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody RegisterMerchantRequest request) {

        MerchantProfile profile = merchantService.registerMerchant(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Merchant registered successfully",
                        MerchantProfileResponse.from(profile)));
    }

    // ── MS-02: View Merchant Profile ─────────────────────────────────────────
    @GetMapping("/api/v1/merchants/me")
    public ResponseEntity<ApiResponse<MerchantProfileResponse>> getMyProfile(
            @RequestHeader("X-User-Id") UUID userId) {

        MerchantProfile profile = merchantService.getMerchantProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(MerchantProfileResponse.from(profile)));
    }

    // ── MS-03: Update Merchant Profile ───────────────────────────────────────
    @PatchMapping("/api/v1/merchants/me")
    public ResponseEntity<ApiResponse<MerchantProfileResponse>> updateMyProfile(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody UpdateMerchantRequest request) {

        MerchantProfile updated = merchantService.updateMerchantProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated",
                MerchantProfileResponse.from(updated)));
    }

    // ── MS-04: Manage Inventory — Create Product ──────────────────────────────
    @PostMapping("/api/v1/merchant/inventory")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateProductRequest request) {

        Product product = merchantService.createProduct(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", ProductResponse.from(product)));
    }

    // ── MS-04: Manage Inventory — Update Product ──────────────────────────────
    @PatchMapping("/api/v1/merchant/inventory/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest request) {

        Product product = merchantService.updateProduct(userId, productId, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated", ProductResponse.from(product)));
    }

    // ── MS-04: Manage Inventory — Delete Product ──────────────────────────────
    @DeleteMapping("/api/v1/merchant/inventory/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID productId) {

        merchantService.deleteProduct(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }

    // ── MS-04: Get Inventory List ─────────────────────────────────────────────
    @GetMapping("/api/v1/merchant/inventory")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getInventory(
            @RequestHeader("X-User-Id") UUID userId) {

        List<ProductResponse> products = merchantService.getMerchantInventory(userId)
                .stream().map(ProductResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    // ── MS-05: View Voucher Redemption Interface (dashboard data) ─────────────
    @GetMapping("/api/v1/merchant/vouchers")
    public ResponseEntity<ApiResponse<MerchantProfileResponse>> getVoucherDashboard(
            @RequestHeader("X-User-Id") UUID userId) {

        // Returns merchant profile so the POS app can initialize the scanner
        // Actual redemption records come from voucher-service
        MerchantProfile profile = merchantService.getMerchantProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(MerchantProfileResponse.from(profile)));
    }

    // ── MS-08: View Redemption History ───────────────────────────────────────
    // Note: Full redemption history lives in voucher-service.
    // This endpoint returns merchant-side price anomalies as merchant history context.
    @GetMapping("/api/v1/merchant/redemptions")
    public ResponseEntity<ApiResponse<List<PriceAnomalyResponse>>> getRedemptionHistory(
            @RequestHeader("X-User-Id") UUID userId) {

        MerchantProfile profile = merchantService.getMerchantProfile(userId);
        List<PriceAnomalyResponse> anomalies = merchantService.getPriceAnomalies(profile.getId())
                .stream().map(PriceAnomalyResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(anomalies));
    }

    // ── MS-10: View Merchant Analytics ───────────────────────────────────────
    @GetMapping("/api/v1/merchant/analytics")
    public ResponseEntity<ApiResponse<MerchantAnalyticsResponse>> getAnalytics(
            @RequestHeader("X-User-Id") UUID userId) {

        MerchantProfile profile = merchantService.getMerchantProfile(userId);
        List<Product> products = merchantService.getProductsByMerchant(profile.getId());
        List<PriceAnomaly> anomalies = merchantService.getPriceAnomalies(profile.getId());

        long available = products.stream().filter(Product::isAvailable).count();
        BigDecimal avgPrice = products.isEmpty() ? BigDecimal.ZERO :
                products.stream()
                        .map(Product::getCurrentPriceEtb)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(products.size()), 2,
                                java.math.RoundingMode.HALF_UP);

        MerchantAnalyticsResponse analytics = MerchantAnalyticsResponse.builder()
                .totalProducts(products.size())
                .availableProducts(available)
                .priceAnomaliesCount(anomalies.size())
                .averageProductPrice(avgPrice)
                .build();

        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    // ── MS-11: Validate Merchant Eligibility (internal REST fallback) ─────────
    @GetMapping("/api/v1/merchants/{merchantId}/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateMerchant(
            @PathVariable UUID merchantId) {

        boolean active = merchantService.isMerchantActive(merchantId);
        return ResponseEntity.ok(ApiResponse.success(active));
    }

    // ── Get merchant by ID (used by investment-service / off-taker-service) ───
    @GetMapping("/api/v1/merchants/{merchantId}")
    public ResponseEntity<ApiResponse<MerchantProfileResponse>> getMerchantById(
            @PathVariable UUID merchantId) {

        MerchantProfile profile = merchantService.getMerchantById(merchantId);
        return ResponseEntity.ok(ApiResponse.success(MerchantProfileResponse.from(profile)));
    }
}
