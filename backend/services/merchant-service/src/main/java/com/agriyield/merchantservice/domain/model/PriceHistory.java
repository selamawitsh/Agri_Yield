package com.agriyield.merchantservice.domain.model;

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
public class PriceHistory {
    private UUID id;
    private UUID productId;
    private BigDecimal oldPriceEtb;
    private BigDecimal newPriceEtb;
    private OffsetDateTime changedAt;
    private UUID changedBy;
}
