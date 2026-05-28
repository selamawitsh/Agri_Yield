package com.agriyield.geospatialservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class YieldPredictionResponse {
    private UUID farmId;
    private String cropType;
    private double predictedYieldMin;
    private double predictedYieldMax;
    private double predictedYieldMean;
    private double totalYieldMinQuintals;
    private double totalYieldMaxQuintals;
    private double totalYieldMeanQuintals;
    private int confidencePct;
    private int weeksToHarvest;
    private String modelVersion;
    private LocalDateTime predictedAt;
}
