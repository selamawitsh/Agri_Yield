package com.agriyield.geospatialservice.presentation.controller;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import com.agriyield.geospatialservice.application.port.outgoing.YieldPredictionRepositoryPort;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;
import com.agriyield.geospatialservice.infrastructure.config.JwtUtils;
import com.agriyield.geospatialservice.presentation.dto.response.ApiResponse;
import com.agriyield.geospatialservice.presentation.dto.response.DigitalTwinResponse;
import com.agriyield.geospatialservice.presentation.dto.response.FarmMapResponse;
import com.agriyield.geospatialservice.presentation.dto.response.NdviReadingResponse;
import com.agriyield.geospatialservice.presentation.dto.response.YieldPredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    // ── Latest NDVI ───────────────────────────────────────────────────────────
    @GetMapping("/farms/{farmId}/ndvi")
    public ResponseEntity<ApiResponse<NdviReadingResponse>> getLatestNdvi(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {
        log.info("GET /geospatial/farms/{}/ndvi", farmId);
        NdviReading r = geospatialService.getLatestNdvi(farmId);
        return ResponseEntity.ok(ApiResponse.success(toNdviResponse(r)));
    }

    // ── NDVI History ──────────────────────────────────────────────────────────
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

    // ── Yield Prediction ──────────────────────────────────────────────────────
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

    // ── Harvest Readiness ─────────────────────────────────────────────────────
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

    // ── Manual NDVI Sync (Admin only) ─────────────────────────────────────────
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

    // ── Farm Map ──────────────────────────────────────────────────────────────
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

    // ── Digital Twin ──────────────────────────────────────────────────────────
    @GetMapping("/farms/{farmId}/digital-twin")
    public ResponseEntity<ApiResponse<DigitalTwinResponse>> getDigitalTwin(
            @RequestHeader("Authorization") String auth,
            @PathVariable UUID farmId) {
        log.info("GET /geospatial/farms/{}/digital-twin", farmId);
        GeospatialServicePort.DigitalTwinData twin = geospatialService.getDigitalTwin(farmId);
        return ResponseEntity.ok(ApiResponse.success(toDigitalTwinResponse(twin)));
    }

    // ── Satellite Image ───────────────────────────────────────────────────────
    // Returns a real true-colour PNG photo of the farm taken by Sentinel-2
    // Band 4 (Red) + Band 3 (Green) + Band 2 (Blue) = natural colour
    // Used by: investor dashboard farm detail page, farmer app My Farm screen
    @GetMapping(value = "/farms/{farmId}/satellite-image",
                produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getSatelliteImage(
            @PathVariable UUID farmId,
            @RequestParam(defaultValue = "512") int width,
            @RequestParam(defaultValue = "512") int height) {
        log.info("GET /geospatial/farms/{}/satellite-image {}x{}px", farmId, width, height);

        int w = Math.min(Math.max(width,  64), 2500);
        int h = Math.min(Math.max(height, 64), 2500);

        byte[] image = geospatialService.getSatelliteImage(farmId, w, h);

        if (image == null || image.length == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header("Cache-Control", "public, max-age=86400")
                .header("Content-Disposition", "inline; filename=farm-" + farmId + ".png")
                .body(image);
    }

    // ── Private mappers ───────────────────────────────────────────────────────

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

    private DigitalTwinResponse toDigitalTwinResponse(GeospatialServicePort.DigitalTwinData d) {
        DigitalTwinResponse.SpatialLayer spatial = DigitalTwinResponse.SpatialLayer.builder()
                .geoJsonPolygon(d.spatialLayer().geoJsonPolygon())
                .centroidLat(d.spatialLayer().centroidLat())
                .centroidLng(d.spatialLayer().centroidLng())
                .areaHectares(d.spatialLayer().areaHectares())
                .satelliteVerified(d.spatialLayer().satelliteVerified())
                .build();

        List<DigitalTwinResponse.NdviPoint> ndviPoints = d.ndviLayer().timeSeries()
                .stream()
                .map(p -> DigitalTwinResponse.NdviPoint.builder()
                        .date(p.date())
                        .ndviValue(p.ndviValue())
                        .healthStatus(p.healthStatus())
                        .build())
                .collect(Collectors.toList());

        DigitalTwinResponse.NdviLayer ndvi = DigitalTwinResponse.NdviLayer.builder()
                .currentNdvi(d.ndviLayer().currentNdvi())
                .healthStatus(d.ndviLayer().healthStatus())
                .peakNdvi30Days(d.ndviLayer().peakNdvi30Days())
                .ndviTrend(d.ndviLayer().ndviTrend())
                .timeSeries(ndviPoints)
                .build();

        DigitalTwinResponse.WeatherLayer weather = DigitalTwinResponse.WeatherLayer.builder()
                .totalRainfallMm30Days(d.weatherLayer().totalRainfallMm30Days())
                .avgTempC(d.weatherLayer().avgTempC())
                .droughtRiskScore(d.weatherLayer().droughtRiskScore())
                .weatherRiskLevel(d.weatherLayer().weatherRiskLevel())
                .droughtTriggered(d.weatherLayer().droughtTriggered())
                .build();

        DigitalTwinResponse.YieldLayer yield = DigitalTwinResponse.YieldLayer.builder()
                .predictedYieldMinQuintals(d.yieldLayer().predictedYieldMinQuintals())
                .predictedYieldMaxQuintals(d.yieldLayer().predictedYieldMaxQuintals())
                .predictedYieldMeanQuintals(d.yieldLayer().predictedYieldMeanQuintals())
                .confidencePct(d.yieldLayer().confidencePct())
                .weeksToHarvest(d.yieldLayer().weeksToHarvest())
                .modelVersion(d.yieldLayer().modelVersion())
                .build();

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
