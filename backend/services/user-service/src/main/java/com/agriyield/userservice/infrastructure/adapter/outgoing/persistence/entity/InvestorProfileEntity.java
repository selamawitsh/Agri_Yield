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
    
    @Column(name = "agri_score", nullable = false)
    private Integer agriScore;
    
    @Column(name = "cooperative_id")
    private UUID cooperativeId;
    
    @Column(name = "telebird_account", length = 30)
    private String telebirdAccount;
    
    @Column(name = "total_seasons_completed", nullable = false)
    private Integer totalSeasonsCompleted;
    
    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage;
    
    @Column(name = "risk_tolerance", nullable = false, length = 20)
    private String riskTolerance;
    
    @Column(name = "investment_goal", length = 50)
    private String investmentGoal;
    
    @Column(name = "total_invested_etb", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalInvestedEtb;
    
    @Column(name = "total_returned_etb", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalReturnedEtb;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
