package com.agriyield.offtakerservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmOpportunity {
    private UUID id;
    private UUID farmId;
    private String farmerId;
    private String cropType;
    private BigDecimal areaHectares;
    private String region;
    private String kebeleCode;
    private BigDecimal gpsCentroidLat;
    private BigDecimal gpsCentroidLng;
    private int agriScore;
    private String cropCycleId;
    private String cropCycleStatus;
    private BigDecimal currentNdvi;
    private String ndviHealthStatus;
    private BigDecimal predictedYieldMinQuintals;
    private BigDecimal predictedYieldMaxQuintals;
    private BigDecimal predictedYieldMeanQuintals;
    private Integer yieldConfidencePct;
    private boolean harvestReady;
    private String estimatedHarvestDateFrom;
    private String estimatedHarvestDateTo;
    private int existingBidsCount;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime createdAt;
}
