package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity;

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

@Entity
@Table(name = "investor_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "telebird_account", length = 30)
    private String telebirdAccount;

    @Builder.Default
    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage = "am";

    @Builder.Default
    @Column(name = "risk_tolerance", nullable = false, length = 20)
    private String riskTolerance = "MODERATE";

    @Column(name = "investment_goal", length = 50)
    private String investmentGoal;

    @Builder.Default
    @Column(name = "total_invested_etb", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInvestedEtb = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_returned_etb", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalReturnedEtb = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}