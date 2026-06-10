package com.agriyield.voucherservice.presentation.controller;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;
import com.agriyield.voucherservice.infrastructure.config.JwtUtils;
import com.agriyield.voucherservice.presentation.dto.request.RedeemVoucherRequest;
import com.agriyield.voucherservice.presentation.dto.response.ApiResponse;
import com.agriyield.voucherservice.presentation.dto.response.VoucherRedemptionResponse;
import com.agriyield.voucherservice.presentation.dto.response.VoucherResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherServicePort voucherService;
    private final JwtUtils jwtUtils;

    /** Farmer: view my vouchers */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getMyVouchers(
            @RequestHeader("Authorization") String authHeader) {
        UUID farmerId = jwtUtils.extractUserId(authHeader);
        log.info("GET /api/v1/vouchers/my — farmer: {}", farmerId);
        List<VoucherResponse> vouchers = voucherService.getMyVouchers(farmerId)
            .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(vouchers));
    }

    /**
     * Merchant: view all vouchers redeemed by this merchant.
     * Used by the merchant POS app for transaction history.
     * Returns all vouchers where merchantId matches the authenticated user.
     */
    @GetMapping("/merchant/my")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getMyMerchantVouchers(
            @RequestHeader("Authorization") String authHeader) {
        UUID merchantId = jwtUtils.extractUserId(authHeader);
        log.info("GET /api/v1/vouchers/merchant/my — merchant: {}", merchantId);
        List<VoucherResponse> vouchers = voucherService.getByMerchantId(merchantId)
            .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(vouchers));
    }

    /** Get voucher by ID */
    @GetMapping("/{voucherId}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID voucherId) {
        log.info("GET /api/v1/vouchers/{}", voucherId);
        Voucher voucher = voucherService.getById(voucherId);
        return ResponseEntity.ok(ApiResponse.success(toResponse(voucher)));
    }

    /** Get voucher by code */
    @GetMapping("/code/{voucherCode}")
    public ResponseEntity<ApiResponse<VoucherResponse>> getByCode(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String voucherCode) {
        log.info("GET /api/v1/vouchers/code/{}", voucherCode);
        Voucher voucher = voucherService.getByCode(voucherCode);
        return ResponseEntity.ok(ApiResponse.success(toResponse(voucher)));
    }

    /** Merchant: redeem a voucher at POS */
    @PostMapping("/redeem")
    public ResponseEntity<ApiResponse<VoucherRedemptionResponse>> redeem(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody RedeemVoucherRequest request) {
        UUID merchantId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/vouchers/redeem — merchant: {}, code: {}",
            merchantId, request.getVoucherCode());
        VoucherRedemption redemption = voucherService.redeem(
            request.getVoucherCode(),
            merchantId,
            merchantId,
            request.getNotes());
        return ResponseEntity.ok(ApiResponse.success(
            "Voucher redeemed successfully. Funds released from escrow.",
            toRedemptionResponse(redemption)));
    }

    /** Get redemption history for a voucher */
    @GetMapping("/{voucherId}/redemptions")
    public ResponseEntity<ApiResponse<List<VoucherRedemptionResponse>>> getRedemptions(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID voucherId) {
        log.info("GET /api/v1/vouchers/{}/redemptions", voucherId);
        List<VoucherRedemptionResponse> redemptions = voucherService.getRedemptions(voucherId)
            .stream().map(this::toRedemptionResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(redemptions));
    }

    /** Get all vouchers for a farm */
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> getByFarmId(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID farmId) {
        log.info("GET /api/v1/vouchers/farm/{}", farmId);
        List<VoucherResponse> vouchers = voucherService.getByFarmId(farmId)
            .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(vouchers));
    }

    private VoucherResponse toResponse(Voucher v) {
        return VoucherResponse.builder()
            .id(v.getId())
            .voucherCode(v.getVoucherCode())
            .investmentId(v.getInvestmentId())
            .farmId(v.getFarmId())
            .farmerId(v.getFarmerId())
            .merchantId(v.getMerchantId())
            .inputNeedId(v.getInputNeedId())
            .cropCycleId(v.getCropCycleId())
            .productName(v.getProductName())
            .productCategory(v.getProductCategory().getValue())
            .amountEtb(v.getAmountEtb())
            .status(v.getStatus().getValue())
            .issuedAt(v.getIssuedAt())
            .redeemedAt(v.getRedeemedAt())
            .expiresAt(v.getExpiresAt())
            .createdAt(v.getCreatedAt())
            .build();
    }

    private VoucherRedemptionResponse toRedemptionResponse(VoucherRedemption r) {
        return VoucherRedemptionResponse.builder()
            .id(r.getId())
            .voucherId(r.getVoucherId())
            .merchantId(r.getMerchantId())
            .redeemedBy(r.getRedeemedBy())
            .amountEtb(r.getAmountEtb())
            .escrowReleased(r.getEscrowReleased())
            .notes(r.getNotes())
            .redeemedAt(r.getRedeemedAt())
            .build();
    }
}
