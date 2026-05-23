package com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "payout_records")
public class PayoutRecordEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "investment_id", nullable = false)
    private UUID investmentId;

    @Column(name = "investor_id", nullable = false)
    private UUID investorId;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "listing_id", nullable = false)
    private UUID listingId;

    @Column(name = "principal_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal principalEtb;

    @Column(name = "return_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal returnEtb;

    @Column(name = "total_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalEtb;

    @Column(name = "actual_apr", nullable = false, precision = 5, scale = 2)
    private BigDecimal actualApr;

    @Column(name = "payout_reason", length = 255)
    private String payoutReason;

    @Column(name = "paid_at", nullable = false)
    private LocalDateTime paidAt;
}
