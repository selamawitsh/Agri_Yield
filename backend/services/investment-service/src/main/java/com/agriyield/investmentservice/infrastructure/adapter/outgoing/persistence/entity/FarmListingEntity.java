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
@Table(name = "farm_listings")
public class FarmListingEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "input_need_id", nullable = false, unique = true)
    private UUID inputNeedId;

    @Column(name = "crop_cycle_id", nullable = false)
    private UUID cropCycleId;

    @Column(name = "crop_type", nullable = false, length = 30)
    private String cropType;

    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "kebele_code", nullable = false, length = 20)
    private String kebeleCode;

    @Column(name = "season_name", nullable = false, length = 50)
    private String seasonName;

    @Column(name = "total_amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmountEtb;

    @Column(name = "funded_amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal fundedAmountEtb;

    @Column(name = "funding_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal fundingPct;

    @Column(name = "base_apr", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseApr;

    @Column(name = "current_apr", nullable = false, precision = 5, scale = 2)
    private BigDecimal currentApr;

    @Column(name = "ndvi_bonus", precision = 5, scale = 2)
    private BigDecimal ndviBonus;

    @Column(name = "weather_bonus", precision = 5, scale = 2)
    private BigDecimal weatherBonus;

    @Column(name = "ndvi_penalty", precision = 5, scale = 2)
    private BigDecimal ndviPenalty;

    @Column(name = "drought_risk", precision = 5, scale = 2)
    private BigDecimal droughtRisk;

    @Column(name = "agri_score", nullable = false)
    private int agriScore;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "funding_deadline")
    private LocalDateTime fundingDeadline;

    @Column(name = "fully_funded_at")
    private LocalDateTime fullyFundedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
