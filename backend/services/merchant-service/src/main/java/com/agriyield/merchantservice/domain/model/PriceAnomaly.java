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
public class PriceAnomaly {
    private UUID id;
    private UUID merchantId;
    private UUID productId;
    private BigDecimal merchantPriceEtb;
    private BigDecimal regionalMedianEtb;
    private BigDecimal deviationPct;
    private OffsetDateTime flaggedAt;
    private OffsetDateTime resolvedAt;
}
