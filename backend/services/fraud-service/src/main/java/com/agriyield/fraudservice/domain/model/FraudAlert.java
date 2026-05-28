package com.agriyield.fraudservice.domain.model;

import com.agriyield.fraudservice.domain.enums.EntityType;
import com.agriyield.fraudservice.domain.enums.FraudAlertType;
import com.agriyield.fraudservice.domain.enums.FraudSeverity;
import com.agriyield.fraudservice.domain.exception.BusinessException;
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
public class FraudAlert {

    private UUID id;
    private FraudAlertType alertType;
    private EntityType entityType;
    private UUID entityId;
    private FraudSeverity severity;
    private String description;
    private String evidence;
    private boolean resolved;
    private UUID resolvedByAdminId;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Admin resolves a fraud alert */
    public void resolve(UUID adminId, String notes) {
        if (this.resolved) {
            throw new BusinessException(
                "Fraud alert is already resolved", "ALERT_ALREADY_RESOLVED");
        }
        this.resolved = true;
        this.resolvedByAdminId = adminId;
        this.resolutionNotes = notes;
        this.resolvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCritical() {
        return this.severity == FraudSeverity.CRITICAL;
    }

    public boolean isHighOrCritical() {
        return this.severity == FraudSeverity.HIGH
            || this.severity == FraudSeverity.CRITICAL;
    }
}
