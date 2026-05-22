package com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "investments")
public class InvestmentEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "investor_id", nullable = false)
    private UUID investorId;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "input_need_id", nullable = false)
    private UUID inputNeedId;

    @Column(name = "crop_cycle_id", nullable = false)
    private UUID cropCycleId;

    @Column(name = "amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountEtb;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "crop_type", nullable = false, length = 30)
    private String cropType;

    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "season_name", nullable = false, length = 50)
    private String seasonName;

    @Column(name = "expected_return_pct", precision = 5, scale = 2)
    private BigDecimal expectedReturnPct;

    @Column(name = "actual_return_pct", precision = 5, scale = 2)
    private BigDecimal actualReturnPct;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancelled_reason", length = 255)
    private String cancelledReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
