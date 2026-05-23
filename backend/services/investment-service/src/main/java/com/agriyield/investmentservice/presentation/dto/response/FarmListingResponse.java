package com.agriyield.investmentservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FarmListingResponse {

    private UUID id;
    private UUID farmId;
    private UUID farmerId;
    private UUID inputNeedId;
    private UUID cropCycleId;
    private String cropType;
    private String region;
    private String kebeleCode;
    private String seasonName;
    private BigDecimal totalAmountEtb;
    private BigDecimal fundedAmountEtb;
    private BigDecimal fundingPct;
    private BigDecimal currentApr;
    private BigDecimal baseApr;
    private int agriScore;
    private String status;
    private LocalDateTime fundingDeadline;
    private LocalDateTime fullyFundedAt;
    private LocalDateTime createdAt;
}
