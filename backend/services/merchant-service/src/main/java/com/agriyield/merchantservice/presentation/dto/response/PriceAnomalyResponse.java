package com.agriyield.merchantservice.presentation.dto.response;

import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PriceAnomalyResponse {
    private UUID id;
    private UUID merchantId;
    private UUID productId;
    private BigDecimal merchantPriceEtb;
    private BigDecimal regionalMedianEtb;
    private BigDecimal deviationPct;
    private OffsetDateTime flaggedAt;
    private OffsetDateTime resolvedAt;

    public static PriceAnomalyResponse from(PriceAnomaly domain) {
        return PriceAnomalyResponse.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .productId(domain.getProductId())
                .merchantPriceEtb(domain.getMerchantPriceEtb())
                .regionalMedianEtb(domain.getRegionalMedianEtb())
                .deviationPct(domain.getDeviationPct())
                .flaggedAt(domain.getFlaggedAt())
                .resolvedAt(domain.getResolvedAt())
                .build();
    }
}
