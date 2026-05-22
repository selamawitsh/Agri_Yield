package com.agriyield.escrowservice.presentation.controller;

import com.agriyield.escrowservice.application.port.incoming.EscrowServicePort;
import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.domain.model.EscrowRelease;
import com.agriyield.escrowservice.domain.model.EscrowTransaction;
import com.agriyield.escrowservice.infrastructure.config.JwtUtils;
import com.agriyield.escrowservice.presentation.dto.request.ReleasePartialRequest;
import com.agriyield.escrowservice.presentation.dto.response.ApiResponse;
import com.agriyield.escrowservice.presentation.dto.response.EscrowAccountResponse;
import com.agriyield.escrowservice.presentation.dto.response.EscrowReleaseResponse;
import com.agriyield.escrowservice.presentation.dto.response.EscrowTransactionResponse;
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
@RequestMapping("/api/v1/escrow")
@RequiredArgsConstructor
public class EscrowController {

    private final EscrowServicePort escrowService;
    private final JwtUtils jwtUtils;

    @GetMapping("/investment/{investmentId}")
    public ResponseEntity<ApiResponse<EscrowAccountResponse>> getByInvestmentId(
            @PathVariable UUID investmentId) {
        log.info("GET /api/v1/escrow/investment/{}", investmentId);
        EscrowAccount account = escrowService.getByInvestmentId(investmentId);
        return ResponseEntity.ok(ApiResponse.success(toAccountResponse(account)));
    }

    @PostMapping("/investment/{investmentId}/release")
    public ResponseEntity<ApiResponse<EscrowReleaseResponse>> releasePartial(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID investmentId,
            @Valid @RequestBody ReleasePartialRequest request) {
        log.info("POST /api/v1/escrow/investment/{}/release", investmentId);
        // Only internal admin or system calls should reach this endpoint
        EscrowRelease release = escrowService.releasePartial(
            investmentId,
            request.getVoucherId(),
            request.getAmountEtb(),
            request.getReleaseReason());
        return ResponseEntity.ok(ApiResponse.success(
            "Funds released successfully", toReleaseResponse(release)));
    }

    @PostMapping("/investment/{investmentId}/cancel")
    public ResponseEntity<ApiResponse<EscrowAccountResponse>> cancel(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID investmentId) {
        log.info("POST /api/v1/escrow/investment/{}/cancel", investmentId);
        EscrowAccount account = escrowService.cancel(investmentId);
        return ResponseEntity.ok(ApiResponse.success(
            "Escrow cancelled successfully", toAccountResponse(account)));
    }

    @GetMapping("/investment/{investmentId}/transactions")
    public ResponseEntity<ApiResponse<List<EscrowTransactionResponse>>> getTransactions(
            @PathVariable UUID investmentId) {
        log.info("GET /api/v1/escrow/investment/{}/transactions", investmentId);
        List<EscrowTransaction> transactions = escrowService.getTransactions(investmentId);
        List<EscrowTransactionResponse> responses = transactions.stream()
            .map(this::toTransactionResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    private EscrowAccountResponse toAccountResponse(EscrowAccount account) {
        return EscrowAccountResponse.builder()
            .id(account.getId())
            .investmentId(account.getInvestmentId())
            .farmerId(account.getFarmerId())
            .investorId(account.getInvestorId())
            .totalAmountEtb(account.getTotalAmountEtb())
            .lockedAmountEtb(account.getLockedAmountEtb())
            .releasedAmountEtb(account.getReleasedAmountEtb())
            .remainingAmountEtb(account.getRemainingLockedAmountEtb())
            .status(account.getStatus().getValue())
            .lockExpiresAt(account.getLockExpiresAt())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }

    private EscrowReleaseResponse toReleaseResponse(EscrowRelease release) {
        return EscrowReleaseResponse.builder()
            .id(release.getId())
            .escrowAccountId(release.getEscrowAccountId())
            .voucherId(release.getVoucherId())
            .amountEtb(release.getAmountEtb())
            .releaseReason(release.getReleaseReason())
            .releasedAt(release.getReleasedAt())
            .build();
    }

    private EscrowTransactionResponse toTransactionResponse(EscrowTransaction tx) {
        return EscrowTransactionResponse.builder()
            .id(tx.getId())
            .escrowAccountId(tx.getEscrowAccountId())
            .transactionType(tx.getTransactionType().getValue())
            .amountEtb(tx.getAmountEtb())
            .referenceId(tx.getReferenceId())
            .description(tx.getDescription())
            .createdAt(tx.getCreatedAt())
            .build();
    }
}
