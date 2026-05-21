package com.agriyield.farmservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InputNeedResponse {

    private UUID id;
    private UUID farmId;
    private UUID cropCycleId;
    private BigDecimal totalAmountEtb;
    private BigDecimal fundedAmountEtb;
    private String status;
    private LocalDateTime createdAt;
    private List<InputNeedItemResponse> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InputNeedItemResponse {
        private UUID id;
        private String productCategory;
        private String productName;
        private BigDecimal quantity;
        private String unit;
        private BigDecimal estimatedPriceEtb;
        private Integer sequenceOrder;
    }
}
