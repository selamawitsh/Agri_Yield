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
public class InvestmentResponse {

    private UUID id;
    private UUID investorId;
    private UUID farmId;
    private UUID farmerId;
    private UUID inputNeedId;
    private UUID cropCycleId;
    private BigDecimal amountEtb;
    private String status;
    private String cropType;
    private String region;
    private String seasonName;
    private BigDecimal expectedReturnPct;
    private BigDecimal actualReturnPct;
    private String notes;
    private String cancelledReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
