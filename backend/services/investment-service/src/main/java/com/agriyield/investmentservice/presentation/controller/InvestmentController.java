package com.agriyield.investmentservice.presentation.controller;

import com.agriyield.investmentservice.application.port.incoming.InvestmentServicePort;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.infrastructure.config.JwtUtils;
import com.agriyield.investmentservice.presentation.dto.request.CancelInvestmentRequest;
import com.agriyield.investmentservice.presentation.dto.request.PlaceInvestmentRequest;
import com.agriyield.investmentservice.presentation.dto.response.ApiResponse;
import com.agriyield.investmentservice.presentation.dto.response.InvestmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentServicePort investmentService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<ApiResponse<InvestmentResponse>> placeInvestment(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody PlaceInvestmentRequest request) {

        UUID investorId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/investments — investor: {}", investorId);

        Investment investment = investmentService.placeInvestment(
            investorId,
            request.getFarmId(),
            request.getInputNeedId(),
            request.getAmountEtb(),
            request.getNotes());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Investment placed successfully",
                toResponse(investment)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<InvestmentResponse>>> getMyInvestments(
            @RequestHeader("Authorization") String authHeader) {

        UUID investorId = jwtUtils.extractUserId(authHeader);
        log.info("GET /api/v1/investments/my — investor: {}", investorId);

        List<InvestmentResponse> responses = investmentService
            .getMyInvestments(investorId)
            .stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{investmentId}")
    public ResponseEntity<ApiResponse<InvestmentResponse>> getById(
            @PathVariable UUID investmentId) {
        log.info("GET /api/v1/investments/{}", investmentId);
        Investment investment = investmentService.getById(investmentId);
        return ResponseEntity.ok(ApiResponse.success(toResponse(investment)));
    }

    @PostMapping("/{investmentId}/cancel")
    public ResponseEntity<ApiResponse<InvestmentResponse>> cancel(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID investmentId,
            @RequestBody(required = false) CancelInvestmentRequest request) {

        UUID investorId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/investments/{}/cancel — investor: {}", investmentId, investorId);

        String reason = request != null ? request.getReason() : "Cancelled by investor";
        Investment investment = investmentService.cancel(investmentId, investorId, reason);

        return ResponseEntity.ok(ApiResponse.success(
            "Investment cancelled successfully", toResponse(investment)));
    }

    private InvestmentResponse toResponse(Investment investment) {
        return InvestmentResponse.builder()
            .id(investment.getId())
            .investorId(investment.getInvestorId())
            .farmId(investment.getFarmId())
            .farmerId(investment.getFarmerId())
            .inputNeedId(investment.getInputNeedId())
            .cropCycleId(investment.getCropCycleId())
            .amountEtb(investment.getAmountEtb())
            .status(investment.getStatus().getValue())
            .cropType(investment.getCropType())
            .region(investment.getRegion())
            .seasonName(investment.getSeasonName())
            .expectedReturnPct(investment.getExpectedReturnPct())
            .actualReturnPct(investment.getActualReturnPct())
            .notes(investment.getNotes())
            .cancelledReason(investment.getCancelledReason())
            .createdAt(investment.getCreatedAt())
            .updatedAt(investment.getUpdatedAt())
            .build();
    }
}
