package com.agriyield.geospatialservice.application.port.incoming;

import com.agriyield.geospatialservice.domain.model.FarmBoundary;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;

import java.util.List;
import java.util.UUID;

public interface GeospatialServicePort {

    // ── SRS §3.6: StartMonitoring ─────────────────────────────────────────
    // Called when farm.registered event arrives — kicks off NDVI tracking
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

    // ── SRS §3.6: GetFarmContext (for offtaker browsing) ──────────────────
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

    // ── Scheduled jobs (called by scheduler) ─────────────────────────────
    void syncNdviAllActiveFarms();
    void runWeeklyYieldPredictions();
    void runHarvestReadinessDetection();

    // ── Manual NDVI sync for a single farm ────────────────────────────────
    NdviReading syncNdviForFarm(UUID farmId);

    // ── GS-01: Register / store farm polygon ──────────────────────────────
    record RegisterPolygonResult(boolean success, String message, double areaHectares) {}

    RegisterPolygonResult registerFarmPolygon(UUID farmId, String geoJsonPolygon,
                                              double centroidLat, double centroidLng,
                                              Double areaHectares);

    // ── GS-02: Validate polygon geometry ────────────────────────────────
    record PolygonValidationResult(
        boolean valid,
        String message,
        double areaHectares,
        double centroidLat,
        double centroidLng
    ) {}

    PolygonValidationResult validatePolygon(String geoJsonPolygon);

    // ── GS-03: Calculate farm area from stored polygon ────────────────────
    record FarmAreaResult(double areaHectares, double areaSqKm) {}

    FarmAreaResult calculateFarmArea(UUID farmId);

    // ── GS-09: Detect spatial overlap with existing farms ─────────────────
    record SpatialOverlapResult(
        boolean hasOverlap,
        double overlapPercentage,
        UUID conflictingFarmId,
        String message
    ) {}

    SpatialOverlapResult detectSpatialOverlap(UUID farmId, String geoJsonPolygon,
                                              double centroidLat, double centroidLng);

    // ── GS-11: Farm map data for UI ───────────────────────────────────────
    record FarmMapData(
        UUID farmId,
        String geoJsonPolygon,
        double centroidLat,
        double centroidLng,
        double areaHectares,
        NdviReading latestNdvi
    ) {}

    FarmMapData getFarmMap(UUID farmId);
}
