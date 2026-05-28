package com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.fraudservice.domain.enums.EntityType;
import com.agriyield.fraudservice.domain.enums.FraudAlertType;
import com.agriyield.fraudservice.domain.enums.FraudSeverity;
import com.agriyield.fraudservice.domain.model.FraudAlert;
import com.agriyield.fraudservice.domain.model.FraudRiskScore;
import com.agriyield.fraudservice.domain.model.GpsLog;
import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.entity.FraudAlertEntity;
import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.entity.FraudRiskScoreEntity;
import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.entity.GpsLogEntity;
import org.springframework.stereotype.Component;

@Component
public class FraudEntityMapper {

    // ── FraudAlert ────────────────────────────────────────────────────────────

    public FraudAlert toDomain(FraudAlertEntity e) {
        if (e == null) return null;
        return FraudAlert.builder()
            .id(e.getId())
            .alertType(FraudAlertType.valueOf(e.getAlertType()))
            .entityType(EntityType.fromValue(e.getEntityType()))
            .entityId(e.getEntityId())
            .severity(FraudSeverity.fromValue(e.getSeverity()))
            .description(e.getDescription())
            .evidence(e.getEvidence())
            .resolved(Boolean.TRUE.equals(e.getResolved()))
            .resolvedByAdminId(e.getResolvedByAdminId())
            .resolutionNotes(e.getResolutionNotes())
            .resolvedAt(e.getResolvedAt())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    public FraudAlertEntity toEntity(FraudAlert d) {
        if (d == null) return null;
        return FraudAlertEntity.builder()
            .id(d.getId())
            .alertType(d.getAlertType().getValue())
            .entityType(d.getEntityType().getValue())
            .entityId(d.getEntityId())
            .severity(d.getSeverity().getValue())
            .description(d.getDescription())
            .evidence(d.getEvidence())
            .resolved(d.isResolved())
            .resolvedByAdminId(d.getResolvedByAdminId())
            .resolutionNotes(d.getResolutionNotes())
            .resolvedAt(d.getResolvedAt())
            .createdAt(d.getCreatedAt())
            .updatedAt(d.getUpdatedAt())
            .build();
    }

    // ── FraudRiskScore ────────────────────────────────────────────────────────

    public FraudRiskScore toDomain(FraudRiskScoreEntity e) {
        if (e == null) return null;
        return FraudRiskScore.builder()
            .id(e.getId())
            .entityId(e.getEntityId())
            .entityType(EntityType.fromValue(e.getEntityType()))
            .gpsAnomalyScore(e.getGpsAnomalyScore())
            .duplicateVoucherScore(e.getDuplicateVoucherScore())
            .exifMismatchScore(e.getExifMismatchScore())
            .suspiciousActivityScore(e.getSuspiciousActivityScore())
            .totalScore(e.getTotalScore())
            .severity(FraudSeverity.fromValue(e.getSeverity()))
            .calculatedAt(e.getCalculatedAt())
            .createdAt(e.getCreatedAt())
            .updatedAt(e.getUpdatedAt())
            .build();
    }

    public FraudRiskScoreEntity toEntity(FraudRiskScore d) {
        if (d == null) return null;
        return FraudRiskScoreEntity.builder()
            .id(d.getId())
            .entityId(d.getEntityId())
            .entityType(d.getEntityType().getValue())
            .gpsAnomalyScore(d.getGpsAnomalyScore())
            .duplicateVoucherScore(d.getDuplicateVoucherScore())
            .exifMismatchScore(d.getExifMismatchScore())
            .suspiciousActivityScore(d.getSuspiciousActivityScore())
            .totalScore(d.getTotalScore())
            .severity(d.getSeverity().getValue())
            .calculatedAt(d.getCalculatedAt())
            .createdAt(d.getCreatedAt())
            .updatedAt(d.getUpdatedAt())
            .build();
    }

    // ── GpsLog ────────────────────────────────────────────────────────────────

    public GpsLog toDomain(GpsLogEntity e) {
        if (e == null) return null;
        return GpsLog.builder()
            .id(e.getId())
            .entityId(e.getEntityId())
            .entityType(e.getEntityType())
            .latitude(e.getLatitude())
            .longitude(e.getLongitude())
            .context(e.getContext())
            .flagged(Boolean.TRUE.equals(e.getFlagged()))
            .flagReason(e.getFlagReason())
            .recordedAt(e.getRecordedAt())
            .createdAt(e.getCreatedAt())
            .build();
    }

    public GpsLogEntity toEntity(GpsLog d) {
        if (d == null) return null;
        return GpsLogEntity.builder()
            .id(d.getId())
            .entityId(d.getEntityId())
            .entityType(d.getEntityType())
            .latitude(d.getLatitude())
            .longitude(d.getLongitude())
            .context(d.getContext())
            .flagged(d.isFlagged())
            .flagReason(d.getFlagReason())
            .recordedAt(d.getRecordedAt())
            .createdAt(d.getCreatedAt())
            .build();
    }
}
