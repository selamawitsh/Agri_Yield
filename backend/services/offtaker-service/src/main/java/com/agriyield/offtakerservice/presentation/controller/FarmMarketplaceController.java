package com.agriyield.offtakerservice.presentation.controller;

import com.agriyield.offtakerservice.application.port.outgoing.GeospatialServicePort;
import com.agriyield.offtakerservice.application.port.outgoing.FarmServicePort;
import com.agriyield.offtakerservice.infrastructure.config.JwtUtils;
import com.agriyield.offtakerservice.presentation.dto.response.ApiResponse;
import com.agriyield.offtakerservice.presentation.dto.response.FarmMarketplaceResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/offtaker/farms")
@RequiredArgsConstructor
public class FarmMarketplaceController {

    private final GeospatialServicePort geospatialService;
    private final FarmServicePort farmService;
    private final JwtUtils jwtUtils;

    // UC-OFF-01: Browse Available Farms
    // Full filtering would be backed by a search index in production.
    // Here we provide direct farm lookup via gRPC to geospatial-service.
    @GetMapping("/{farmId}")
    public ResponseEntity<ApiResponse<FarmMarketplaceResponse>> getFarmDetail(
            @PathVariable String farmId,
            HttpServletRequest request) {

        jwtUtils.extractUserId(request); // validates caller is authenticated

        Map<String, Object> ctx = geospatialService.getFarmContext(farmId);
        Map<String, Object> harvest = geospatialService.getHarvestReadiness(farmId);

        FarmMarketplaceResponse response = FarmMarketplaceResponse.builder()
                .farmId((String) ctx.get("farmId"))
                .farmerId((String) ctx.get("farmerId"))
                .cropType((String) ctx.get("cropType"))
                .areaHectares(toDouble(ctx.get("areaHectares")))
                .region((String) ctx.get("region"))
                .kebeleCode((String) ctx.get("kebeleCode"))
                .gpsCentroidLat(toDouble(ctx.get("gpsCentroidLat")))
                .gpsCentroidLng(toDouble(ctx.get("gpsCentroidLng")))
                .agriScore(toInt(ctx.get("agriScore")))
                .cropCycleId((String) ctx.get("cropCycleId"))
                .cropCycleStatus((String) ctx.get("cropCycleStatus"))
                .currentNdvi(toDouble(ctx.get("currentNdvi")))
                .ndviHealthStatus((String) ctx.get("ndviHealthStatus"))
                .predictedYieldMeanQuintals(toDouble(ctx.get("predictedYieldMeanQuintals")))
                .yieldConfidencePct(toInt(ctx.get("yieldConfidencePct")))
                .harvestReady((Boolean) harvest.getOrDefault("isReady", false))
                .estimatedHarvestFrom((String) harvest.get("estimatedDateFrom"))
                .estimatedHarvestTo((String) harvest.get("estimatedDateTo"))
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number n) return n.doubleValue();
        return Double.parseDouble(val.toString());
    }

    private int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Number n) return n.intValue();
        return Integer.parseInt(val.toString());
    }
}
