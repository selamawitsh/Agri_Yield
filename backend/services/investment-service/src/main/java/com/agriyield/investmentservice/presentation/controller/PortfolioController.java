package com.agriyield.investmentservice.presentation.controller;

import com.agriyield.investmentservice.application.port.incoming.ListingServicePort;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.domain.model.PayoutRecord;
import com.agriyield.investmentservice.infrastructure.config.JwtUtils;
import com.agriyield.investmentservice.presentation.dto.response.ApiResponse;
import com.agriyield.investmentservice.presentation.dto.response.InvestmentResponse;
import com.agriyield.investmentservice.presentation.dto.response.PayoutRecordResponse;
import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.FarmJourneyEventEntity;
import com.agriyield.investmentservice.infrastructure.repository.JpaFarmJourneyEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final ListingServicePort listingService;
    private final JwtUtils jwtUtils;
    private final JpaFarmJourneyEventRepository journeyRepository;

    /** IS-07: View investor portfolio */
    @GetMapping
    public ResponseEntity<ApiResponse<List<InvestmentResponse>>> getPortfolio(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        UUID investorId = jwtUtils.extractUserId(authHeader);
        log.info("GET /api/v1/portfolio — investor: {}", investorId);

        List<InvestmentResponse> portfolio = listingService.getPortfolio(investorId)
            .stream().map(this::toResponse).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(portfolio));
    }

    /** IS-08: View investment details */
    @GetMapping("/{investmentId}")
    public ResponseEntity<ApiResponse<InvestmentResponse>> getInvestmentDetails(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable UUID investmentId) {

        jwtUtils.extractUserId(authHeader); // validate token
        log.info("GET /api/v1/portfolio/{}", investmentId);
        Investment investment = listingService.getInvestmentDetails(investmentId);
        return ResponseEntity.ok(ApiResponse.success(toResponse(investment)));
    }

    /** IS-12: Payout history */
    @GetMapping("/payouts")
    public ResponseEntity<ApiResponse<List<PayoutRecordResponse>>> getPayouts(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        UUID investorId = jwtUtils.extractUserId(authHeader);
        log.info("GET /api/v1/portfolio/payouts — investor: {}", investorId);

        List<PayoutRecordResponse> payouts = listingService.getPayoutHistory(investorId)
            .stream().map(this::toPayoutResponse).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(payouts));
    }

    private InvestmentResponse toResponse(Investment i) {
        return InvestmentResponse.builder()
            .id(i.getId())
            .investorId(i.getInvestorId())
            .farmId(i.getFarmId())
            .farmerId(i.getFarmerId())
            .inputNeedId(i.getInputNeedId())
            .cropCycleId(i.getCropCycleId())
            .amountEtb(i.getAmountEtb())
            .status(i.getStatus().getValue())
            .cropType(i.getCropType())
            .region(i.getRegion())
            .seasonName(i.getSeasonName())
            .expectedReturnPct(i.getExpectedReturnPct())
            .actualReturnPct(i.getActualReturnPct())
            .payoutAmountEtb(i.getPayoutAmountEtb())
            .payoutAt(i.getPayoutAt())
            .notes(i.getNotes())
            .cancelledReason(i.getCancelledReason())
            .createdAt(i.getCreatedAt())
            .updatedAt(i.getUpdatedAt())
            .build();
    }

    /** IS-13: Farm journey timeline for investor tracking */
    @GetMapping("/journey/{farmId}")
    public ResponseEntity<ApiResponse<java.util.List<java.util.Map<String, Object>>>> getJourney(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable java.util.UUID farmId) {
        jwtUtils.extractUserId(authHeader);
        java.util.List<java.util.Map<String, Object>> events = journeyRepository
                .findByFarmIdOrderByOccurredAtAsc(farmId)
                .stream()
                .map(e -> {
                    java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("eventType", e.getEventType());
                    m.put("occurredAt", e.getOccurredAt().toString());
                    m.put("eventData", e.getEventData());
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    private PayoutRecordResponse toPayoutResponse(PayoutRecord p) {
        return PayoutRecordResponse.builder()
            .id(p.getId())
            .investmentId(p.getInvestmentId())
            .farmId(p.getFarmId())
            .listingId(p.getListingId())
            .principalEtb(p.getPrincipalEtb())
            .returnEtb(p.getReturnEtb())
            .totalEtb(p.getTotalEtb())
            .actualApr(p.getActualApr())
            .payoutReason(p.getPayoutReason())
            .paidAt(p.getPaidAt())
            .build();
    }
}
