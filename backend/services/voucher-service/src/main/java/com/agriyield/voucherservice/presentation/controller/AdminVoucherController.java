package com.agriyield.voucherservice.presentation.controller;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import com.agriyield.voucherservice.application.port.outgoing.InvestmentServicePort;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.presentation.dto.response.ApiResponse;
import com.agriyield.voucherservice.presentation.dto.response.VoucherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/vouchers")
@RequiredArgsConstructor
public class AdminVoucherController {

    private final VoucherServicePort voucherService;

    private final InvestmentServicePort investmentService;

    /**
     * Manually trigger voucher generation for an investment.
     * Body must include: investment_id, farm_id, farmer_id, input_need_id, crop_cycle_id
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> generate(@RequestBody Map<String, String> body) {
        log.info("POST /api/v1/admin/vouchers/generate body: {}", body);

        try {
            UUID investmentId = UUID.fromString(body.get("investment_id"));
            UUID farmId = UUID.fromString(body.get("farm_id"));
            UUID farmerId = UUID.fromString(body.get("farmer_id"));
            UUID inputNeedId = UUID.fromString(body.get("input_need_id"));
            UUID cropCycleId = UUID.fromString(body.get("crop_cycle_id"));

            List<Voucher> vouchers = voucherService.generateForInvestment(
                investmentId, farmId, farmerId, inputNeedId, cropCycleId
            );

            List<VoucherResponse> response = vouchers.stream().map(v -> VoucherResponse.builder()
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
                .build()).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Vouchers generated", response));
        } catch (Exception e) {
            log.error("Admin voucher generation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error("Admin voucher generation failed: " + e.getMessage()));
        }
    }
}
