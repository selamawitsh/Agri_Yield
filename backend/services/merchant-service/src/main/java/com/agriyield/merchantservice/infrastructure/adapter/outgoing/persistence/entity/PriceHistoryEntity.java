package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "price_history", schema = "merchant_service")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "old_price_etb", nullable = false, precision = 12, scale = 2)
    private BigDecimal oldPriceEtb;

    @Column(name = "new_price_etb", nullable = false, precision = 12, scale = 2)
    private BigDecimal newPriceEtb;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false)
    private OffsetDateTime changedAt;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;
}
