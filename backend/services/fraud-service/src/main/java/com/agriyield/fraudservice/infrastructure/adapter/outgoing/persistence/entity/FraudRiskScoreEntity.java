package com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fraud_risk_scores",
    uniqueConstraints = @UniqueConstraint(columnNames = {"entity_id", "entity_type"}))
public class FraudRiskScoreEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType;

    @Column(name = "gps_anomaly_score", nullable = false)
    private Integer gpsAnomalyScore;

    @Column(name = "duplicate_voucher_score", nullable = false)
    private Integer duplicateVoucherScore;

    @Column(name = "exif_mismatch_score", nullable = false)
    private Integer exifMismatchScore;

    @Column(name = "suspicious_activity_score", nullable = false)
    private Integer suspiciousActivityScore;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(name = "severity", nullable = false, length = 20)
    private String severity;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
