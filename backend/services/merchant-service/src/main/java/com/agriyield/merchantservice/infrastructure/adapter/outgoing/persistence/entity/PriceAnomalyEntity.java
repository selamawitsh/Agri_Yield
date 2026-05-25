package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "price_anomalies", schema = "merchant_service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceAnomalyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "merchant_price_etb", nullable = false, precision = 12, scale = 2)
    private BigDecimal merchantPriceEtb;

    @Column(name = "regional_median_etb", nullable = false, precision = 12, scale = 2)
    private BigDecimal regionalMedianEtb;

    @Column(name = "deviation_pct", nullable = false, precision = 6, scale = 2)
    private BigDecimal deviationPct;

    @CreationTimestamp
    @Column(name = "flagged_at", nullable = false)
    private OffsetDateTime flaggedAt;

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;
}
