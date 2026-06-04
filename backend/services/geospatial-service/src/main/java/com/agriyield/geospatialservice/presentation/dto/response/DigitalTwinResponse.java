package com.agriyield.geospatialservice.presentation.dto.response;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * GS-06: REST response DTO for the Digital Twin endpoint.
 *
 * GET /api/v1/geospatial/farms/{farmId}/digital-twin
 *
 * Mirrors GeospatialServicePort.DigitalTwinData but as a serialisable
 * Lombok @Data class so Jackson renders it cleanly as JSON.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalTwinResponse {

    private UUID   farmId;
    private String cropType;
    private String region;
    private String seasonName;
    private String cropCycleStatus;
    private int    agriScore;

    private SpatialLayer  spatialLayer;
    private NdviLayer     ndviLayer;
    private WeatherLayer  weatherLayer;
    private YieldLayer    yieldLayer;
    private HarvestLayer  harvestLayer;

    private String generatedAt;

    // ── Nested DTOs ───────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SpatialLayer {
        private String  geoJsonPolygon;
        private double  centroidLat;
        private double  centroidLng;
        private double  areaHectares;
        private boolean satelliteVerified;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NdviLayer {
        private double          currentNdvi;
        private String          healthStatus;
        private double          peakNdvi30Days;
        private double          ndviTrend;
        private List<NdviPoint> timeSeries;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NdviPoint {
        private String date;
        private double ndviValue;
        private String healthStatus;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class WeatherLayer {
        private double  totalRainfallMm30Days;
        private double  avgTempC;
        private int     droughtRiskScore;
        private String  weatherRiskLevel;
        private boolean droughtTriggered;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class YieldLayer {
        private double predictedYieldMinQuintals;
        private double predictedYieldMaxQuintals;
        private double predictedYieldMeanQuintals;
        private int    confidencePct;
        private int    weeksToHarvest;
        private String modelVersion;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class HarvestLayer {
        private boolean harvestReady;
        private String  estimatedHarvestFrom;
        private String  estimatedHarvestTo;
        private String  readinessSignal;
    }
}