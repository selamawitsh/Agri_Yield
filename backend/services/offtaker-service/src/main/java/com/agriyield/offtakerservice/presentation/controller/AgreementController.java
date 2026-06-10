package com.agriyield.offtakerservice.presentation.controller;

import com.agriyield.offtakerservice.application.port.incoming.AgreementServicePort;
import com.agriyield.offtakerservice.domain.model.PurchaseAgreement;
import com.agriyield.offtakerservice.infrastructure.config.JwtUtils;
import com.agriyield.offtakerservice.presentation.dto.response.AgreementResponse;
import com.agriyield.offtakerservice.presentation.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agreements")
@RequiredArgsConstructor
public class AgreementController {

    private final AgreementServicePort agreementService;
    private final JwtUtils jwtUtils;

    @GetMapping("/{agreementId}")
    public ResponseEntity<ApiResponse<AgreementResponse>> getAgreement(
            @PathVariable UUID agreementId,
            HttpServletRequest request) {
        jwtUtils.extractUserId(request);
        return ResponseEntity.ok(ApiResponse.success(
                toResponse(agreementService.getById(agreementId))));
    }

    // UC-OFF-06: Sign Purchase Agreement  — FARMER or OFF_TAKER
    @PostMapping("/{agreementId}/sign")
    public ResponseEntity<ApiResponse<AgreementResponse>> signAgreement(
            @PathVariable UUID agreementId,
            HttpServletRequest request) {
        UUID userId = jwtUtils.extractUserId(request);
        String role = jwtUtils.extractUserRole(request);
        PurchaseAgreement agreement = agreementService.signAgreement(agreementId, userId, role);
        return ResponseEntity.ok(ApiResponse.success(
                agreement.isFullyExecuted()
                        ? "Agreement fully executed — both parties signed"
                        : "Signature recorded",
                toResponse(agreement)));
    }

    private AgreementResponse toResponse(PurchaseAgreement a) {
        return AgreementResponse.builder()
                .id(a.getId())
                .bidId(a.getBidId())
                .contractHash(a.getContractHash())
                .contractPdfUrl(a.getContractPdfUrl())
                .farmerSignedAt(a.getFarmerSignedAt())
                .offtakerSignedAt(a.getOfftakerSignedAt())
                .fullyExecuted(a.isFullyExecuted())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
