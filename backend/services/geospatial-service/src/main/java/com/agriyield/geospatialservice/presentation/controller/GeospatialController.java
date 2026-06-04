package com.agriyield.geospatialservice.presentation.controller;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import com.agriyield.geospatialservice.application.port.outgoing.YieldPredictionRepositoryPort;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;
import com.agriyield.geospatialservice.infrastructure.config.JwtUtils;
import com.agriyield.geospatialservice.presentation.dto.response.ApiResponse;
import com.agriyield.geospatialservice.presentation.dto.response.FarmMapResponse;
import com.agriyield.geospatialservice.presentation.dto.response.NdviReadingResponse;
import com.agriyield.geospatialservice.presentation.dto.response.YieldPredictionResponse;
import com.agriyield.geospatialservice.presentation.dto.response.DigitalTwinResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/geospatial")
@RequiredArgsConstructor
public class GeospatialController {

    private final GeospatialServicePort geospatialService;
    private final YieldPredictionRepositoryPort yieldRepository;
    private final JwtUtils jwtUtils;

    @GetMapping("/farms/{farmId}/ndvi")
    public ResponseEntity<ApiResponse<NdviReadingResponse>> getLatestNdvi(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {
        log.info("GET /geospatial/farms/{}/ndvi", farmId);
        NdviReading r = geospatialService.getLatestNdvi(farmId);
        return ResponseEntity.ok(ApiResponse.success(toNdviResponse(r)));
    }

    @GetMapping("/ndvi-history/{farmId}")
    public ResponseEntity<ApiResponse<List<NdviReadingResponse>>> getNdviHistory(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId,
            @RequestParam(defaultValue = "90") int days) {
        log.info("GET /geospatial/ndvi-history/{} days={}", farmId, days);
        List<NdviReadingResponse> readings = geospatialService
            .getNdviTimeSeries(farmId, days)
            .stream().map(this::toNdviResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(readings));
    }

    @GetMapping("/farms/{farmId}/ndvi/history")
    public ResponseEntity<ApiResponse<List<NdviReadingResponse>>> getNdviHistoryAlias(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId,
            @RequestParam(defaultValue = "90") int days) {
        return getNdviHistory(auth, farmId, days);
    }

    @GetMapping("/farms/{farmId}/yield")
    public ResponseEntity<ApiResponse<YieldPredictionResponse>> getYieldPrediction(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {
        log.info("GET /geospatial/farms/{}/yield", farmId);
        return yieldRepository.findLatestByFarmId(farmId)
            .map(y -> ResponseEntity.ok(ApiResponse.success(toYieldResponse(y))))
            .orElse(ResponseEntity.ok(
                ApiResponse.<YieldPredictionResponse>builder()
                    .success(false)
                    .message("No yield prediction available yet")
                    .build()));
    }

    @GetMapping("/farms/{farmId}/harvest-readiness")
    public ResponseEntity<ApiResponse<GeospatialServicePort.HarvestReadinessResult>>
            getHarvestReadiness(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {
        log.info("GET /geospatial/farms/{}/harvest-readiness", farmId);
        GeospatialServicePort.HarvestReadinessResult result =
            geospatialService.predictHarvestReadiness(farmId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/farms/{farmId}/ndvi/sync")
    public ResponseEntity<ApiResponse<NdviReadingResponse>> syncNdvi(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {
        String role = jwtUtils.extractRole(auth);
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403)
                .body(ApiResponse.<NdviReadingResponse>builder()
                    .success(false).message("Admin access required").build());
        }
        log.info("POST /geospatial/farms/{}/ndvi/sync", farmId);
        NdviReading r = geospatialService.syncNdviForFarm(farmId);
        if (r == null) {
            return ResponseEntity.ok(ApiResponse.<NdviReadingResponse>builder()
                .success(false)
                .message("No suitable Sentinel-2 scene found (cloud cover or no data)")
                .build());
        }
        return ResponseEntity.ok(ApiResponse.success("NDVI synced", toNdviResponse(r)));
    }

    @GetMapping("/farm-map/{farmId}")
    public ResponseEntity<ApiResponse<FarmMapResponse>> getFarmMap(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {
        log.info("GET /geospatial/farm-map/{}", farmId);
        GeospatialServicePort.FarmMapData map = geospatialService.getFarmMap(farmId);
        FarmMapResponse response = FarmMapResponse.builder()
            .farmId(map.farmId())
            .geoJsonPolygon(map.geoJsonPolygon())
            .centroidLat(map.centroidLat())
            .centroidLng(map.centroidLng())
            .areaHectares(map.areaHectares())
            .latestNdvi(map.latestNdvi() != null ? toNdviResponse(map.latestNdvi()) : null)
            .build();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private NdviReadingResponse toNdviResponse(NdviReading r) {
        return NdviReadingResponse.builder()
            .farmId(r.getFarmId())
            .ndviValue(r.getNdviValue())
            .cloudCoverage(r.getCloudCoverage())
            .healthStatus(r.getHealthStatus())
            .sentinelSceneId(r.getSentinelSceneId())
            .recordedDate(r.getRecordedDate())
            .build();
    }

    private YieldPredictionResponse toYieldResponse(YieldPrediction p) {
        return YieldPredictionResponse.builder()
            .farmId(p.getFarmId())
            .cropType(p.getCropType())
            .predictedYieldMin(p.getPredictedYieldMin())
            .predictedYieldMax(p.getPredictedYieldMax())
            .predictedYieldMean(p.getPredictedYieldMean())
            .totalYieldMinQuintals(p.getTotalYieldMinQuintals())
            .totalYieldMaxQuintals(p.getTotalYieldMaxQuintals())
            .totalYieldMeanQuintals(p.getTotalYieldMeanQuintals())
            .confidencePct(p.getConfidencePct())
            .weeksToHarvest(p.getWeeksToHarvest())
            .modelVersion(p.getModelVersion())
            .predictedAt(p.getPredictedAt())
            .build();
    }

    // ═══════════════════════════════════════════════════════════════════════════
// ADD THIS METHOD to GeospatialController.java
//
// 1. Add this import at the top of GeospatialController.java:
//
//
// 2. Paste this method after the getFarmMap() method.
// ═══════════════════════════════════════════════════════════════════════════

    // ── GS-06: Digital Twin ───────────────────────────────────────────────────
    // Returns a full composite snapshot of the farm combining:
    //   spatial layer   → GeoJSON polygon, area, satellite verification
    //   NDVI layer      → current value, health, 30-day time series, trend
    //   weather layer   → rainfall, temperature, drought risk
    //   yield layer     → predicted harvest range and confidence
    //   harvest layer   → readiness signal and estimated window
    //
    // Consumers: farmer Flutter app (farm detail screen),
    //            investor Next.js dashboard (portfolio farm card),
    //            offtaker dashboard (procurement planning)
    @GetMapping("/farms/{farmId}/digital-twin")
    public ResponseEntity<ApiResponse<DigitalTwinResponse>> getDigitalTwin(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {

        log.info("GET /geospatial/farms/{}/digital-twin", farmId);

        GeospatialServicePort.DigitalTwinData twin =
                geospatialService.getDigitalTwin(farmId);

        DigitalTwinResponse response = toDigitalTwinResponse(twin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── Mapping helper ────────────────────────────────────────────────────────

    private DigitalTwinResponse toDigitalTwinResponse(
            GeospatialServicePort.DigitalTwinData d) {

        // Spatial layer
        DigitalTwinResponse.SpatialLayer spatial = DigitalTwinResponse.SpatialLayer.builder()
                .geoJsonPolygon(d.spatialLayer().geoJsonPolygon())
                .centroidLat(d.spatialLayer().centroidLat())
                .centroidLng(d.spatialLayer().centroidLng())
                .areaHectares(d.spatialLayer().areaHectares())
                .satelliteVerified(d.spatialLayer().satelliteVerified())
                .build();

        // NDVI layer — map NdviPoint records to DTO
        List<DigitalTwinResponse.NdviPoint> ndviPoints = d.ndviLayer().timeSeries()
                .stream()
                .map(p -> DigitalTwinResponse.NdviPoint.builder()
                        .date(p.date())
                        .ndviValue(p.ndviValue())
                        .healthStatus(p.healthStatus())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        DigitalTwinResponse.NdviLayer ndvi = DigitalTwinResponse.NdviLayer.builder()
                .currentNdvi(d.ndviLayer().currentNdvi())
                .healthStatus(d.ndviLayer().healthStatus())
                .peakNdvi30Days(d.ndviLayer().peakNdvi30Days())
                .ndviTrend(d.ndviLayer().ndviTrend())
                .timeSeries(ndviPoints)
                .build();

        // Weather layer
        DigitalTwinResponse.WeatherLayer weather = DigitalTwinResponse.WeatherLayer.builder()
                .totalRainfallMm30Days(d.weatherLayer().totalRainfallMm30Days())
                .avgTempC(d.weatherLayer().avgTempC())
                .droughtRiskScore(d.weatherLayer().droughtRiskScore())
                .weatherRiskLevel(d.weatherLayer().weatherRiskLevel())
                .droughtTriggered(d.weatherLayer().droughtTriggered())
                .build();

        // Yield layer
        DigitalTwinResponse.YieldLayer yield = DigitalTwinResponse.YieldLayer.builder()
                .predictedYieldMinQuintals(d.yieldLayer().predictedYieldMinQuintals())
                .predictedYieldMaxQuintals(d.yieldLayer().predictedYieldMaxQuintals())
                .predictedYieldMeanQuintals(d.yieldLayer().predictedYieldMeanQuintals())
                .confidencePct(d.yieldLayer().confidencePct())
                .weeksToHarvest(d.yieldLayer().weeksToHarvest())
                .modelVersion(d.yieldLayer().modelVersion())
                .build();

        // Harvest layer
        DigitalTwinResponse.HarvestLayer harvest = DigitalTwinResponse.HarvestLayer.builder()
                .harvestReady(d.harvestLayer().harvestReady())
                .estimatedHarvestFrom(d.harvestLayer().estimatedHarvestFrom())
                .estimatedHarvestTo(d.harvestLayer().estimatedHarvestTo())
                .readinessSignal(d.harvestLayer().readinessSignal())
                .build();

        return DigitalTwinResponse.builder()
                .farmId(d.farmId())
                .cropType(d.cropType())
                .region(d.region())
                .seasonName(d.seasonName())
                .cropCycleStatus(d.cropCycleStatus())
                .agriScore(d.agriScore())
                .spatialLayer(spatial)
                .ndviLayer(ndvi)
                .weatherLayer(weather)
                .yieldLayer(yield)
                .harvestLayer(harvest)
                .generatedAt(d.generatedAt())
                .build();
    }
}
