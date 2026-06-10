package com.agriyield.offtakerservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FarmMarketplaceResponse {
    private String farmId;
    private String farmerId;
    private String cropType;
    private double areaHectares;
    private String region;
    private String kebeleCode;
    private double gpsCentroidLat;
    private double gpsCentroidLng;
    private int agriScore;
    private String cropCycleId;
    private String cropCycleStatus;
    private double currentNdvi;
    private String ndviHealthStatus;
    private double predictedYieldMeanQuintals;
    private int yieldConfidencePct;
    // Harvest readiness
    private boolean harvestReady;
    private String estimatedHarvestFrom;
    private String estimatedHarvestTo;
}
