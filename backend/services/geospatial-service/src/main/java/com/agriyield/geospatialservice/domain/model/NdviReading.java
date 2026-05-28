package com.agriyield.geospatialservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NdviReading {

    private UUID id;
    private UUID farmId;
    private double ndviValue;
    private double cloudCoverage;
    private String sentinelSceneId;
    private LocalDate recordedDate;
    private LocalDateTime createdAt;

    /**
     * SRS §3.6.1: NDVI health classification
     */
    public String getHealthStatus() {
        if (ndviValue < 0.2)  return "POOR";
        if (ndviValue < 0.4)  return "MODERATE";
        if (ndviValue < 0.6)  return "GOOD";
        return "EXCELLENT";
    }

    /**
     * SRS §3.6.3: harvest readiness — NDVI has peaked and dropped > 0.08
     */
    public static boolean isHarvestReady(double peakNdvi, double currentNdvi, double threshold) {
        return (peakNdvi - currentNdvi) > threshold;
    }

    /**
     * SRS §3.6.1: NDVI = (NIR - Red) / (NIR + Red)
     * band values are reflectance 0.0-1.0
     */
    public static double calculate(double nir, double red) {
        double denom = nir + red;
        if (denom == 0) return 0.0;
        return (nir - red) / denom;
    }
}
