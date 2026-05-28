package com.agriyield.fraudservice.domain.model;

import com.agriyield.fraudservice.domain.enums.EntityType;
import com.agriyield.fraudservice.domain.enums.FraudSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudRiskScore {

    private UUID id;
    private UUID entityId;
    private EntityType entityType;

    // Component scores per SRS FR-06 formula:
    // fraud_risk_score = gps_anomaly + duplicate_voucher_risk
    //                  + exif_mismatch + suspicious_activity
    private int gpsAnomalyScore;
    private int duplicateVoucherScore;
    private int exifMismatchScore;
    private int suspiciousActivityScore;
    private int totalScore;

    private FraudSeverity severity;
    private LocalDateTime calculatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Recalculate total and severity from components */
    public void recalculate() {
        this.totalScore = this.gpsAnomalyScore
            + this.duplicateVoucherScore
            + this.exifMismatchScore
            + this.suspiciousActivityScore;
        this.severity = FraudSeverity.fromScore(this.totalScore);
        this.calculatedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
