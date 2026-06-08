package com.agriyield.geospatialservice.application.port.incoming;

import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;

import java.util.List;
import java.util.UUID;

public interface GeospatialServicePort {

    // ── SRS §3.6: StartMonitoring ─────────────────────────────────────────
    boolean startMonitoring(UUID farmId);

    // ── SRS §3.6: GetLatestNdvi ───────────────────────────────────────────
    NdviReading getLatestNdvi(UUID farmId);

    // ── SRS §3.6: GetNdviTimeSeries ───────────────────────────────────────
    List<NdviReading> getNdviTimeSeries(UUID farmId, int limitDays);

    // ── SRS §3.6: PredictHarvestReadiness ────────────────────────────────
    record HarvestReadinessResult(
            boolean ready,
            String estimatedDateFrom,
            String estimatedDateTo,
            double currentNdvi,
            double peakNdvi,
            String readinessSignal
    ) {}

    HarvestReadinessResult predictHarvestReadiness(UUID farmId);

    // ── SRS §3.6: GetFarmContext ───────────────────────────────────────────
    record FarmContext(
            String farmId,
            String farmerId,
            String cropType,
            double areaHectares,
            String region,
            String kebeleCode,
            double gpsCentroidLat,
            double gpsCentroidLng,
            int agriScore,
            String cropCycleId,
            String seasonName,
            String cropCycleStatus,
            double currentNdvi,
            String ndviHealthStatus,
            double predictedYieldMeanQuintals,
            int yieldConfidencePct
    ) {}

    FarmContext getFarmContext(UUID farmId);

    // ── SRS §3.6: VerifyFarmBoundary ──────────────────────────────────────
    record BoundaryVerificationResult(
            boolean isWithinBoundary,
            double distanceFromBoundaryM,
            String geoJsonPolygon
    ) {}

    BoundaryVerificationResult verifyFarmBoundary(UUID farmId,
                                                  double photoLat,
                                                  double photoLng);

    // ── Scheduled jobs ────────────────────────────────────────────────────
    void syncNdviAllActiveFarms();
    void runWeeklyYieldPredictions();
    void runHarvestReadinessDetection();

    // ── Manual NDVI sync ─────────────────────────────────────────────────
    NdviReading syncNdviForFarm(UUID farmId);

    // ── GS-01: Register farm polygon ─────────────────────────────────────
    record RegisterPolygonResult(boolean success, String message, double areaHectares) {}

    RegisterPolygonResult registerFarmPolygon(UUID farmId, String geoJsonPolygon,
                                              double centroidLat, double centroidLng,
                                              Double areaHectares);

    // ── GS-02: Validate polygon geometry ─────────────────────────────────
    record PolygonValidationResult(
            boolean valid,
            String message,
            double areaHectares,
            double centroidLat,
            double centroidLng
    ) {}

    PolygonValidationResult validatePolygon(String geoJsonPolygon);

    // ── GS-03: Calculate farm area ────────────────────────────────────────
    record FarmAreaResult(double areaHectares, double areaSqKm) {}

    FarmAreaResult calculateFarmArea(UUID farmId);

    // ── GS-09: Detect spatial overlap ────────────────────────────────────
    record SpatialOverlapResult(
            boolean hasOverlap,
            double overlapPercentage,
            UUID conflictingFarmId,
            String message
    ) {}

    SpatialOverlapResult detectSpatialOverlap(UUID farmId, String geoJsonPolygon,
                                              double centroidLat, double centroidLng);

    // ── GS-11: Farm map data ──────────────────────────────────────────────
    record FarmMapData(
            UUID farmId,
            String geoJsonPolygon,
            double centroidLat,
            double centroidLng,
            double areaHectares,
            NdviReading latestNdvi
    ) {}

    FarmMapData getFarmMap(UUID farmId);

    // ── GS-06: Digital Twin ───────────────────────────────────────────────
    // Composite snapshot combining all spatial, satellite, weather, and
    // yield signals into a single response for the farmer/investor UI.
    //
    // Layer breakdown:
    //   spatialLayer   — farm polygon, area, centroid, satellite verification
    //   ndviLayer      — current NDVI value, health status, 30-day time series
    //   weatherLayer   — rainfall, temperature, drought risk, weather risk level
    //   yieldLayer     — predicted yield range, confidence, weeks to harvest
    //   harvestLayer   — harvest readiness signal and estimated window
    //
    record NdviPoint(String date, double ndviValue, String healthStatus) {}

    record DigitalTwinSpatialLayer(
            String geoJsonPolygon,
            double centroidLat,
            double centroidLng,
            double areaHectares,
            boolean satelliteVerified
    ) {}

    record DigitalTwinNdviLayer(
            double currentNdvi,
            String healthStatus,
            double peakNdvi30Days,
            double ndviTrend,          // positive = growing, negative = declining
            List<NdviPoint> timeSeries // last 30 days, oldest first
    ) {}

    record DigitalTwinWeatherLayer(
            double totalRainfallMm30Days,
            double avgTempC,
            int droughtRiskScore,       // 0–100
            String weatherRiskLevel,    // LOW / MEDIUM / HIGH
            boolean droughtTriggered
    ) {}

    record DigitalTwinYieldLayer(
            double predictedYieldMinQuintals,
            double predictedYieldMaxQuintals,
            double predictedYieldMeanQuintals,
            int confidencePct,
            int weeksToHarvest,
            String modelVersion
    ) {}

    record DigitalTwinHarvestLayer(
            boolean harvestReady,
            String estimatedHarvestFrom,
            String estimatedHarvestTo,
            String readinessSignal
    ) {}

    record DigitalTwinData(
            UUID farmId,
            String cropType,
            String region,
            String seasonName,
            String cropCycleStatus,
            int agriScore,
            DigitalTwinSpatialLayer spatialLayer,
            DigitalTwinNdviLayer ndviLayer,
            DigitalTwinWeatherLayer weatherLayer,
            DigitalTwinYieldLayer yieldLayer,
            DigitalTwinHarvestLayer harvestLayer,
            String generatedAt
    ) {}

    DigitalTwinData getDigitalTwin(UUID farmId);

    // Fetch real satellite image PNG for display in investor dashboard and farmer app
    byte[] getSatelliteImage(UUID farmId, int widthPx, int heightPx);

}