package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity;

import com.agriyield.offtakerservice.domain.enums.BidStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bids")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "offtaker_id", nullable = false)
    private UUID offtakerId;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "crop_cycle_id", nullable = false)
    private UUID cropCycleId;

    @Column(name = "quantity_quintals", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantityQuintals;

    @Column(name = "price_per_quintal_etb", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerQuintalEtb;

    // GENERATED columns — insertable=false, updatable=false
    @Column(name = "total_value_etb", insertable = false, updatable = false, precision = 15, scale = 2)
    private BigDecimal totalValueEtb;

    @Column(name = "bid_deposit_etb", insertable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal bidDepositEtb;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BidStatus status;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
