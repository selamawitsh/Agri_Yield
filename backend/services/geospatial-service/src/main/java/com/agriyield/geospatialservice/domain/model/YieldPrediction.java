package com.agriyield.geospatialservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YieldPrediction {

    private UUID id;
    private UUID farmId;
    private UUID cropCycleId;
    private String cropType;

    // Per-hectare values (quintals/ha)
    private double predictedYieldMin;
    private double predictedYieldMax;
    private double predictedYieldMean;

    // Total farm values (min/mean/max * area_hectares)
    private double totalYieldMinQuintals;
    private double totalYieldMaxQuintals;
    private double totalYieldMeanQuintals;

    private int confidencePct;
    private int weeksToHarvest;
    private String modelVersion;

    // Feature snapshot for audit
    private double ndviPeak;
    private double ndviGrowthRate;
    private double totalRainfallMm;
    private double avgTempC;
    private double altitudeM;
    private String inputQuality;

    private LocalDateTime predictedAt;
    private LocalDateTime createdAt;
}
