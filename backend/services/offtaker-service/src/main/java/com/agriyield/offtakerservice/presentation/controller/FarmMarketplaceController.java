package com.agriyield.offtakerservice.presentation.controller;

import com.agriyield.offtakerservice.application.port.incoming.BidServicePort;
import com.agriyield.offtakerservice.application.port.outgoing.FarmOpportunityRepositoryPort;
import com.agriyield.offtakerservice.application.port.outgoing.GeospatialServicePort;
import com.agriyield.offtakerservice.application.port.outgoing.UserServicePort;
import com.agriyield.offtakerservice.domain.model.Bid;
import com.agriyield.offtakerservice.domain.model.FarmOpportunity;
import com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException;
import com.agriyield.offtakerservice.infrastructure.config.JwtUtils;
import com.agriyield.offtakerservice.presentation.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/offtaker/farms")
@RequiredArgsConstructor
public class FarmMarketplaceController {

    private final FarmOpportunityRepositoryPort opportunityRepository;
    private final GeospatialServicePort geospatialService;
    private final UserServicePort userService;
    private final BidServicePort bidService;
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FarmMarketplaceResponse>>> browseFarms(
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Boolean harvestReady,
            @RequestParam(required = false) Double minNdvi,
            @RequestParam(required = false) String harvestDateFrom,
            @RequestParam(required = false) String harvestDateTo,
            @RequestParam(required = false) Double minYieldQuintals,
            HttpServletRequest request) {

        jwtUtils.extractUserId(request);

        List<FarmMarketplaceResponse> results = opportunityRepository
                .search(
                        cropType  != null && !cropType.isBlank()  ? cropType  : null,
                        region    != null && !region.isBlank()    ? region    : null,
                        harvestReady,
                        minNdvi,
                        harvestDateFrom,
                        harvestDateTo,
                        minYieldQuintals
                )
                .stream().map(this::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    // UC-OFF-02: View Detailed Farm Information — basic version (kept for compatibility)
    @GetMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmMarketplaceResponse>> getFarmDetail(
            @PathVariable UUID farmId,
            HttpServletRequest request) {

        jwtUtils.extractUserId(request);

        FarmOpportunity opportunity = opportunityRepository.findByFarmId(farmId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Farm not available in marketplace: " + farmId));

        return ResponseEntity.ok(ApiResponse.success(toResponse(opportunity)));
    }

    /**
     * NEW: Full UC-OFF-02 compliant detail endpoint. Returns farm listing data
     * PLUS full NDVI history, existing bid history, and farmer identity
     * (phone, Fayda ID, Agri-Score, seasons completed) in a single call.
     *
     * SRS UC-OFF-02 "Information Displayed" requires: Full NDVI history,
     * Yield prediction, Confidence interval, Farmer Agri-Score, Existing bids,
     * Crop details, Harvest estimates — this endpoint satisfies all of them.
     */
    @GetMapping("/{farmId}/full-detail")
    public ResponseEntity<ApiResponse<FarmDetailResponse>> getFullFarmDetail(
            @PathVariable UUID farmId,
            HttpServletRequest request) {

        jwtUtils.extractUserId(request);

        FarmOpportunity opportunity = opportunityRepository.findByFarmId(farmId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Farm not available in marketplace: " + farmId));

        // Full NDVI history — last 90 days, matching investor dashboard convention
        List<Map<String, Object>> ndviHistory = geospatialService.getNdviHistory(farmId.toString(), 90);

        // Existing bids on this farm
        List<BidResponse> bids = bidService.getBidsForFarm(farmId)
                .stream().map(this::toBidResponse).toList();

        // Farmer identity — phone, Fayda ID, KYC status, Agri-Score, seasons completed.
        // NOTE: user-service has no "full_name" field per SRS §3.2 schema, so phone +
        // Fayda ID are the best available identity/trust signals for an off-taker.
        FarmDetailResponse.FarmerIdentity farmerIdentity = null;
        if (opportunity.getFarmerId() != null && !opportunity.getFarmerId().isBlank()) {
            Map<String, Object> userInfo = userService.getUserById(opportunity.getFarmerId());
            Map<String, Object> farmerProfile = userService.getFarmerProfile(opportunity.getFarmerId());

            if (!userInfo.isEmpty()) {
                farmerIdentity = FarmDetailResponse.FarmerIdentity.builder()
                        .farmerId(opportunity.getFarmerId())
                        .phone((String) userInfo.getOrDefault("phone", ""))
                        .faydaId((String) userInfo.getOrDefault("faydaId", ""))
                        .kycStatus((String) userInfo.getOrDefault("kycStatus", "UNKNOWN"))
                        .agriScore(farmerProfile.isEmpty() ? opportunity.getAgriScore()
                                : (int) farmerProfile.getOrDefault("agriScore", opportunity.getAgriScore()))
                        .totalSeasonsCompleted(farmerProfile.isEmpty() ? 0
                                : (int) farmerProfile.getOrDefault("totalSeasonsCompleted", 0))
                        .build();
            }
        }

        FarmDetailResponse detail = FarmDetailResponse.builder()
                .farm(toResponse(opportunity))
                .ndviHistory(ndviHistory)
                .bids(bids)
                .farmer(farmerIdentity)
                .build();

        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    private FarmMarketplaceResponse toResponse(FarmOpportunity o) {
        return FarmMarketplaceResponse.builder()
                .farmId(o.getFarmId().toString())
                .farmerId(o.getFarmerId())
                .cropType(o.getCropType())
                .areaHectares(o.getAreaHectares() != null ? o.getAreaHectares().doubleValue() : 0.0)
                .region(o.getRegion())
                .kebeleCode(o.getKebeleCode())
                .gpsCentroidLat(o.getGpsCentroidLat() != null ? o.getGpsCentroidLat().doubleValue() : 0.0)
                .gpsCentroidLng(o.getGpsCentroidLng() != null ? o.getGpsCentroidLng().doubleValue() : 0.0)
                .agriScore(o.getAgriScore())
                .cropCycleId(o.getCropCycleId())
                .cropCycleStatus(o.getCropCycleStatus())
                .currentNdvi(o.getCurrentNdvi() != null ? o.getCurrentNdvi().doubleValue() : 0.0)
                .ndviHealthStatus(o.getNdviHealthStatus())
                .predictedYieldMeanQuintals(o.getPredictedYieldMeanQuintals() != null
                        ? o.getPredictedYieldMeanQuintals().doubleValue() : 0.0)
                .yieldConfidencePct(o.getYieldConfidencePct() != null ? o.getYieldConfidencePct() : 0)
                .harvestReady(o.isHarvestReady())
                .estimatedHarvestFrom(o.getEstimatedHarvestDateFrom())
                .estimatedHarvestTo(o.getEstimatedHarvestDateTo())
                .existingBidsCount(o.getExistingBidsCount())
                .build();
    }

    private BidResponse toBidResponse(Bid b) {
        return BidResponse.builder()
                .id(b.getId())
                .offtakerId(b.getOfftakerId())
                .farmId(b.getFarmId())
                .cropCycleId(b.getCropCycleId())
                .quantityQuintals(b.getQuantityQuintals())
                .pricePerQuintalEtb(b.getPricePerQuintalEtb())
                .totalValueEtb(b.getTotalValueEtb())
                .bidDepositEtb(b.getBidDepositEtb())
                .status(b.getStatus().name())
                .expiresAt(b.getExpiresAt())
                .acceptedAt(b.getAcceptedAt())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
