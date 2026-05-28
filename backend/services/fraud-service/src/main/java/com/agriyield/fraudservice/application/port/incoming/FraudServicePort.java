package com.agriyield.fraudservice.application.port.incoming;

import com.agriyield.fraudservice.domain.model.FraudAlert;
import com.agriyield.fraudservice.domain.model.FraudRiskScore;

import java.util.List;
import java.util.UUID;

public interface FraudServicePort {

    // ── FR-02: Validate crop photo EXIF metadata ──────────────────────────
    record ExifValidationResult(
        boolean valid,
        double photoLat,
        double photoLng,
        double farmLat,
        double farmLng,
        double distanceKm,
        String failureReason
    ) {}

    ExifValidationResult validateImageMetadata(
        UUID farmId,
        UUID entityId,
        double photoLat,
        double photoLng,
        String photoTimestamp
    );

    // ── FR-03: Detect duplicate voucher redemption ────────────────────────
    record DuplicateRedemptionResult(
        boolean isDuplicate,
        String firstScanTimestamp,
        String failureReason
    ) {}

    DuplicateRedemptionResult validateVoucherRedemption(
        String voucherCode,
        UUID merchantId
    );

    // ── FR-04: Validate QR cryptographic signature ────────────────────────
    record QrSignatureResult(
        boolean valid,
        String failureReason
    ) {}

    QrSignatureResult validateQrSignature(
        String voucherCode,
        String qrPayload,
        String signature
    );

    // ── FR-05: Detect suspicious GPS activity ─────────────────────────────
    record GpsAnomalyResult(
        boolean suspicious,
        double distanceKm,
        long timeDeltaMinutes,
        String failureReason
    ) {}

    GpsAnomalyResult detectSuspiciousGps(
        UUID entityId,
        String entityType,
        double latitude,
        double longitude,
        String context
    );

    // ── FR-06: Calculate fraud risk score ─────────────────────────────────
    FraudRiskScore calculateFraudRiskScore(UUID entityId, String entityType);

    // ── FR-10: Validate merchant redemption eligibility ───────────────────
    record MerchantEligibilityResult(
        boolean eligible,
        String failureReason,
        int fraudScore
    ) {}

    MerchantEligibilityResult validateMerchantEligibility(UUID merchantId);

    // ── FR-07 / FR-08: Alert management ──────────────────────────────────
    List<FraudAlert> getAlerts(String severity, boolean unresolvedOnly, int page, int size);

    FraudAlert getAlertById(UUID alertId);

    FraudAlert resolveAlert(UUID alertId, UUID adminId, String notes);

    List<FraudAlert> getAlertsByEntity(UUID entityId, String entityType);
}
