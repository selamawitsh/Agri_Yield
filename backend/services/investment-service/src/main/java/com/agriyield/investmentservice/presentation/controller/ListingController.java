package com.agriyield.investmentservice.presentation.controller;

import com.agriyield.investmentservice.application.port.incoming.ListingServicePort;
import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.infrastructure.config.JwtUtils;
import com.agriyield.investmentservice.presentation.dto.request.InvestInListingRequest;
import com.agriyield.investmentservice.presentation.dto.response.ApiResponse;
import com.agriyield.investmentservice.presentation.dto.response.FarmListingResponse;
import com.agriyield.investmentservice.presentation.dto.response.InvestmentResponse;
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
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingServicePort listingService;
    private final JwtUtils jwtUtils;

    /** IS-01/02: Browse and filter farm listings */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FarmListingResponse>>> getListings(
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) BigDecimal minApr,
            @RequestParam(required = false) BigDecimal maxApr) {

        log.info("GET /api/v1/listings — cropType={}, region={}", cropType, region);
        List<FarmListingResponse> listings = listingService
            .getActiveListings(cropType, region, minApr, maxApr)
            .stream().map(this::toListingResponse).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(listings));
    }

    /** IS-03: View listing details */
    @GetMapping("/{listingId}")
    public ResponseEntity<ApiResponse<FarmListingResponse>> getListingById(
            @PathVariable UUID listingId) {

        log.info("GET /api/v1/listings/{}", listingId);
        FarmListing listing = listingService.getListingById(listingId);
        return ResponseEntity.ok(ApiResponse.success(toListingResponse(listing)));
    }

    /** IS-05: Invest in a listing */
    @PostMapping("/{listingId}/invest")
    public ResponseEntity<ApiResponse<InvestmentResponse>> invest(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID listingId,
            @Valid @RequestBody InvestInListingRequest request) {

        UUID investorId = jwtUtils.extractUserId(authHeader);
        log.info("POST /api/v1/listings/{}/invest — investor: {}", listingId, investorId);

        Investment investment = listingService.investInListing(
            listingId, investorId, request.getAmountEtb(), request.getNotes());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Investment placed successfully",
                toInvestmentResponse(investment)));
    }

    /** IS-09: NDVI history for a listing */
    @GetMapping("/{listingId}/ndvi-history")
    public ResponseEntity<ApiResponse<Object>> getNdviHistory(
            @PathVariable UUID listingId) {

        log.info("GET /api/v1/listings/{}/ndvi-history", listingId);
        Object history = listingService.getNdviHistory(listingId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    private FarmListingResponse toListingResponse(FarmListing l) {
        return FarmListingResponse.builder()
            .id(l.getId())
            .farmId(l.getFarmId())
            .farmerId(l.getFarmerId())
            .inputNeedId(l.getInputNeedId())
            .cropCycleId(l.getCropCycleId())
            .cropType(l.getCropType())
            .region(l.getRegion())
            .kebeleCode(l.getKebeleCode())
            .seasonName(l.getSeasonName())
            .totalAmountEtb(l.getTotalAmountEtb())
            .fundedAmountEtb(l.getFundedAmountEtb())
            .fundingPct(l.getFundingPct())
            .currentApr(l.getCurrentApr())
            .baseApr(l.getBaseApr())
            .agriScore(l.getAgriScore())
            .status(l.getStatus().getValue())
            .fundingDeadline(l.getFundingDeadline())
            .fullyFundedAt(l.getFullyFundedAt())
            .createdAt(l.getCreatedAt())
            .build();
    }

    private InvestmentResponse toInvestmentResponse(Investment i) {
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
            .notes(i.getNotes())
            .createdAt(i.getCreatedAt())
            .updatedAt(i.getUpdatedAt())
            .build();
    }
}
