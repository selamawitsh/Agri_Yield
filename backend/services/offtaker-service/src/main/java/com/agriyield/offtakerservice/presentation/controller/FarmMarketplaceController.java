package com.agriyield.offtakerservice.presentation.controller;

import com.agriyield.offtakerservice.application.port.outgoing.FarmOpportunityRepositoryPort;
import com.agriyield.offtakerservice.domain.model.FarmOpportunity;
import com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException;
import com.agriyield.offtakerservice.infrastructure.config.JwtUtils;
import com.agriyield.offtakerservice.presentation.dto.response.ApiResponse;
import com.agriyield.offtakerservice.presentation.dto.response.FarmMarketplaceResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/offtaker/farms")
@RequiredArgsConstructor
public class FarmMarketplaceController {

    private final FarmOpportunityRepositoryPort opportunityRepository;
    private final JwtUtils jwtUtils;

    // UC-OFF-01: Browse Available Farms
    @GetMapping
    public ResponseEntity<ApiResponse<List<FarmMarketplaceResponse>>> browseFarms(
            @RequestParam(required = false) String cropType,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) Boolean harvestReady,
            HttpServletRequest request) {

        jwtUtils.extractUserId(request);

        String crop = (cropType != null && !cropType.isBlank()) ? cropType : null;
        String reg  = (region   != null && !region.isBlank())   ? region   : null;

        List<FarmMarketplaceResponse> results = opportunityRepository
                .search(crop, reg, harvestReady)
                .stream().map(this::toResponse).toList();

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    // UC-OFF-02: View Detailed Farm Information
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

    private FarmMarketplaceResponse toResponse(FarmOpportunity o) {
        return FarmMarketplaceResponse.builder()
                .farmId(o.getFarmId().toString())
                .farmerId(o.getFarmerId())
                .cropType(o.getCropType())
                .areaHectares(o.getAreaHectares() != null
                        ? o.getAreaHectares().doubleValue() : 0.0)
                .region(o.getRegion())
                .kebeleCode(o.getKebeleCode())
                .gpsCentroidLat(o.getGpsCentroidLat() != null
                        ? o.getGpsCentroidLat().doubleValue() : 0.0)
                .gpsCentroidLng(o.getGpsCentroidLng() != null
                        ? o.getGpsCentroidLng().doubleValue() : 0.0)
                .agriScore(o.getAgriScore())
                .cropCycleId(o.getCropCycleId())
                .cropCycleStatus(o.getCropCycleStatus())
                .currentNdvi(o.getCurrentNdvi() != null
                        ? o.getCurrentNdvi().doubleValue() : 0.0)
                .ndviHealthStatus(o.getNdviHealthStatus())
                .predictedYieldMeanQuintals(o.getPredictedYieldMeanQuintals() != null
                        ? o.getPredictedYieldMeanQuintals().doubleValue() : 0.0)
                .yieldConfidencePct(o.getYieldConfidencePct() != null
                        ? o.getYieldConfidencePct() : 0)
                .harvestReady(o.isHarvestReady())
                .estimatedHarvestFrom(o.getEstimatedHarvestDateFrom())
                .estimatedHarvestTo(o.getEstimatedHarvestDateTo())
                .existingBidsCount(o.getExistingBidsCount())
                .build();
    }
}
