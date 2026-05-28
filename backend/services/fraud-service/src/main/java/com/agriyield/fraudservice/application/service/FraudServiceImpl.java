package com.agriyield.fraudservice.application.service;

import com.agriyield.fraudservice.application.port.incoming.FraudServicePort;
import com.agriyield.fraudservice.application.port.outgoing.*;
import com.agriyield.fraudservice.domain.enums.EntityType;
import com.agriyield.fraudservice.domain.enums.FraudAlertType;
import com.agriyield.fraudservice.domain.enums.FraudSeverity;
import com.agriyield.fraudservice.domain.exception.BusinessException;
import com.agriyield.fraudservice.domain.exception.FraudAlertNotFoundException;
import com.agriyield.fraudservice.domain.model.FraudAlert;
import com.agriyield.fraudservice.domain.model.FraudRiskScore;
import com.agriyield.fraudservice.domain.model.GpsLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FraudServiceImpl implements FraudServicePort {

    private final FraudAlertRepositoryPort alertRepository;
    private final FraudRiskScoreRepositoryPort scoreRepository;
    private final GpsLogRepositoryPort gpsLogRepository;
    private final FraudEventPublisherPort eventPublisher;
    private final RedisPort redis;

    // Weights from SRS FR-06 formula
    @Value("${app.fraud.gps-anomaly-weight:30}")
    private int gpsAnomalyWeight;

    @Value("${app.fraud.duplicate-voucher-weight:40}")
    private int duplicateVoucherWeight;

    @Value("${app.fraud.exif-mismatch-weight:20}")
    private int exifMismatchWeight;

    @Value("${app.fraud.suspicious-activity-weight:10}")
    private int suspiciousActivityWeight;

    @Value("${app.fraud.high-risk-threshold:70}")
    private int highRiskThreshold;

    @Value("${app.fraud.critical-risk-threshold:90}")
    private int criticalRiskThreshold;

    @Value("${app.fraud.redis-scan-ttl-hours:24}")
    private int redisScanTtlHours;

    // ── FR-02: Validate crop photo EXIF metadata ─────────────────────────────
    @Override
    @Transactional
    public ExifValidationResult validateImageMetadata(UUID farmId,
                                                       UUID entityId,
                                                       double photoLat,
                                                       double photoLng,
                                                       String photoTimestamp) {
        log.info("FR-02: validateImageMetadata farm={} entity={}", farmId, entityId);

        // Log the GPS submission
        GpsLog gpsLog = GpsLog.builder()
            .id(UUID.randomUUID())
            .entityId(entityId)
            .entityType(EntityType.FARMER.getValue())
            .latitude(photoLat)
            .longitude(photoLng)
            .context("CROP_PHOTO_UPLOAD")
            .flagged(false)
            .recordedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        // Retrieve farm GPS from Redis cache key: farm:gps:{farmId}
        Optional<String> farmGpsCached = redis.get("farm:gps:" + farmId);
        double farmLat = 0.0;
        double farmLng = 0.0;

        if (farmGpsCached.isPresent()) {
            String[] parts = farmGpsCached.get().split(",");
            farmLat = Double.parseDouble(parts[0]);
            farmLng = Double.parseDouble(parts[1]);
        }

        double distKm = GpsLog.distanceKm(photoLat, photoLng, farmLat, farmLng);

        // SRS: allow 500m buffer (0.5km)
        boolean valid = distKm <= 0.5 || farmGpsCached.isEmpty();

        if (!valid) {
            gpsLog.setFlagged(true);
            gpsLog.setFlagReason("Photo GPS " + distKm + "km from farm centroid");

            FraudAlert alert = buildAlert(
                FraudAlertType.EXIF_METADATA_MISMATCH,
                EntityType.FARMER,
                entityId,
                FraudSeverity.MEDIUM,
                "Photo GPS " + String.format("%.2f", distKm) + "km from registered farm",
                "{\"photoLat\":" + photoLat + ",\"photoLng\":" + photoLng
                    + ",\"farmLat\":" + farmLat + ",\"farmLng\":" + farmLng
                    + ",\"distanceKm\":" + distKm + "}"
            );
            alertRepository.save(alert);
            updateFraudScore(entityId, EntityType.FARMER.getValue(), 0, 0, exifMismatchWeight, 0);

            if (alert.isHighOrCritical()) eventPublisher.publishFraudAlertHigh(alert);
        }

        gpsLogRepository.save(gpsLog);

        return new ExifValidationResult(valid, photoLat, photoLng,
            farmLat, farmLng, distKm,
            valid ? null : "Photo GPS does not match farm location");
    }

    // ── FR-03: Detect duplicate voucher redemption ───────────────────────────
    @Override
    @Transactional
    public DuplicateRedemptionResult validateVoucherRedemption(String voucherCode,
                                                                UUID merchantId) {
        log.info("FR-03: validateVoucherRedemption code={} merchant={}", voucherCode, merchantId);

        String redisKey = "voucher:scanned:" + voucherCode;
        Optional<String> existing = redis.get(redisKey);

        if (existing.isPresent()) {
            log.warn("FR-03: DUPLICATE SCAN detected for voucher={}", voucherCode);

            FraudAlert alert = buildAlert(
                FraudAlertType.DUPLICATE_VOUCHER_REDEMPTION,
                EntityType.MERCHANT,
                merchantId,
                FraudSeverity.CRITICAL,
                "Duplicate voucher scan: " + voucherCode,
                "{\"voucherCode\":\"" + voucherCode + "\",\"firstScan\":\""
                    + existing.get() + "\",\"merchantId\":\"" + merchantId + "\"}"
            );
            alertRepository.save(alert);
            eventPublisher.publishFraudAlertCritical(alert);
            updateFraudScore(merchantId, EntityType.MERCHANT.getValue(),
                0, duplicateVoucherWeight, 0, 0);

            return new DuplicateRedemptionResult(true, existing.get(),
                "DUPLICATE_SCAN");
        }

        // Mark as scanned in Redis with 24h TTL (SRS §3.5.3 check #2)
        redis.set(redisKey, LocalDateTime.now().toString(),
            Duration.ofHours(redisScanTtlHours));

        return new DuplicateRedemptionResult(false, null, null);
    }

    // ── FR-04: Validate QR cryptographic signature ───────────────────────────
    @Override
    @Transactional
    public QrSignatureResult validateQrSignature(String voucherCode,
                                                  String qrPayload,
                                                  String signature) {
        log.info("FR-04: validateQrSignature code={}", voucherCode);

        // Signature verification is handled in voucher-service (HMAC-SHA256).
        // Fraud service logs the validation event and raises alert on failure.
        if (signature == null || signature.isBlank()) {
            FraudAlert alert = buildAlert(
                FraudAlertType.INVALID_QR_SIGNATURE,
                EntityType.MERCHANT,
                null,
                FraudSeverity.HIGH,
                "Missing QR signature for voucher: " + voucherCode,
                "{\"voucherCode\":\"" + voucherCode + "\"}"
            );
            alertRepository.save(alert);
            eventPublisher.publishFraudAlertHigh(alert);
            return new QrSignatureResult(false, "INVALID_SIGNATURE");
        }

        return new QrSignatureResult(true, null);
    }

    // ── FR-05: Detect suspicious GPS activity ────────────────────────────────
    @Override
    @Transactional
    public GpsAnomalyResult detectSuspiciousGps(UUID entityId,
                                                  String entityType,
                                                  double latitude,
                                                  double longitude,
                                                  String context) {
        log.info("FR-05: detectSuspiciousGps entity={} type={}", entityId, entityType);

        GpsLog newLog = GpsLog.builder()
            .id(UUID.randomUUID())
            .entityId(entityId)
            .entityType(entityType)
            .latitude(latitude)
            .longitude(longitude)
            .context(context)
            .flagged(false)
            .recordedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();

        Optional<GpsLog> lastLog = gpsLogRepository.findLatestByEntityId(entityId, entityType);

        if (lastLog.isPresent()) {
            GpsLog prev = lastLog.get();
            double distKm = GpsLog.distanceKm(prev.getLatitude(), prev.getLongitude(),
                latitude, longitude);

            long minutesDelta = java.time.Duration.between(
                prev.getRecordedAt(), LocalDateTime.now()).toMinutes();

            // Impossible movement: > 500km in < 60 minutes
            boolean suspicious = distKm > 500 && minutesDelta < 60;

            if (suspicious) {
                newLog.setFlagged(true);
                newLog.setFlagReason(
                    "Impossible movement: " + String.format("%.1f", distKm)
                        + "km in " + minutesDelta + " minutes");

                FraudAlert alert = buildAlert(
                    FraudAlertType.SUSPICIOUS_GPS_MOVEMENT,
                    EntityType.fromValue(entityType),
                    entityId,
                    FraudSeverity.HIGH,
                    newLog.getFlagReason(),
                    "{\"distanceKm\":" + distKm + ",\"minutesDelta\":" + minutesDelta + "}"
                );
                alertRepository.save(alert);
                updateFraudScore(entityId, entityType, gpsAnomalyWeight, 0, 0, 0);
                eventPublisher.publishFraudAlertHigh(alert);

                gpsLogRepository.save(newLog);
                return new GpsAnomalyResult(true, distKm, minutesDelta,
                    "Impossible movement detected");
            }

            gpsLogRepository.save(newLog);
            return new GpsAnomalyResult(false, distKm, minutesDelta, null);
        }

        gpsLogRepository.save(newLog);
        return new GpsAnomalyResult(false, 0, 0, null);
    }

    // ── FR-06: Calculate fraud risk score ────────────────────────────────────
    @Override
    @Transactional
    public FraudRiskScore calculateFraudRiskScore(UUID entityId, String entityType) {
        log.info("FR-06: calculateFraudRiskScore entity={} type={}", entityId, entityType);

        FraudRiskScore score = scoreRepository.findByEntityId(entityId, entityType)
            .orElse(FraudRiskScore.builder()
                .id(UUID.randomUUID())
                .entityId(entityId)
                .entityType(EntityType.fromValue(entityType))
                .gpsAnomalyScore(0)
                .duplicateVoucherScore(0)
                .exifMismatchScore(0)
                .suspiciousActivityScore(0)
                .createdAt(LocalDateTime.now())
                .build());

        score.recalculate();

        FraudRiskScore saved = scoreRepository.save(score);

        // Auto-flag high-risk entities (FR-07)
        if (saved.getTotalScore() >= criticalRiskThreshold) {
            FraudAlert alert = buildAlert(
                FraudAlertType.HIGH_FRAUD_SCORE,
                saved.getEntityType(),
                entityId,
                FraudSeverity.CRITICAL,
                "Fraud risk score " + saved.getTotalScore() + " exceeds critical threshold",
                "{\"totalScore\":" + saved.getTotalScore() + "}"
            );
            alertRepository.save(alert);
            eventPublisher.publishFraudAlertCritical(alert);
        } else if (saved.getTotalScore() >= highRiskThreshold) {
            FraudAlert alert = buildAlert(
                FraudAlertType.HIGH_FRAUD_SCORE,
                saved.getEntityType(),
                entityId,
                FraudSeverity.HIGH,
                "Fraud risk score " + saved.getTotalScore() + " exceeds high threshold",
                "{\"totalScore\":" + saved.getTotalScore() + "}"
            );
            alertRepository.save(alert);
            eventPublisher.publishFraudAlertHigh(alert);
        }

        return saved;
    }

    // ── FR-10: Validate merchant redemption eligibility ──────────────────────
    @Override
    @Transactional(readOnly = true)
    public MerchantEligibilityResult validateMerchantEligibility(UUID merchantId) {
        log.info("FR-10: validateMerchantEligibility merchant={}", merchantId);

        FraudRiskScore score = scoreRepository
            .findByEntityId(merchantId, EntityType.MERCHANT.getValue())
            .orElse(null);

        int fraudScore = score != null ? score.getTotalScore() : 0;

        long unresolvedAlerts = alertRepository.countUnresolvedByEntityId(merchantId);

        if (fraudScore >= criticalRiskThreshold) {
            return new MerchantEligibilityResult(false,
                "Merchant has critical fraud score: " + fraudScore, fraudScore);
        }

        if (unresolvedAlerts >= 3) {
            return new MerchantEligibilityResult(false,
                "Merchant has " + unresolvedAlerts + " unresolved fraud alerts",
                fraudScore);
        }

        return new MerchantEligibilityResult(true, null, fraudScore);
    }

    // ── FR-08: Audit fraud alerts ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<FraudAlert> getAlerts(String severity, boolean unresolvedOnly,
                                       int page, int size) {
        return alertRepository.findAll(severity, unresolvedOnly, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public FraudAlert getAlertById(UUID alertId) {
        return alertRepository.findById(alertId)
            .orElseThrow(() -> new FraudAlertNotFoundException(alertId.toString()));
    }

    @Override
    @Transactional
    public FraudAlert resolveAlert(UUID alertId, UUID adminId, String notes) {
        FraudAlert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new FraudAlertNotFoundException(alertId.toString()));
        alert.resolve(adminId, notes);
        return alertRepository.save(alert);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FraudAlert> getAlertsByEntity(UUID entityId, String entityType) {
        return alertRepository.findByEntityId(entityId, entityType);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private FraudAlert buildAlert(FraudAlertType type,
                                   EntityType entityType,
                                   UUID entityId,
                                   FraudSeverity severity,
                                   String description,
                                   String evidence) {
        return FraudAlert.builder()
            .id(UUID.randomUUID())
            .alertType(type)
            .entityType(entityType)
            .entityId(entityId)
            .severity(severity)
            .description(description)
            .evidence(evidence)
            .resolved(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private void updateFraudScore(UUID entityId, String entityType,
                                   int gpsAdd, int dupAdd,
                                   int exifAdd, int suspAdd) {
        FraudRiskScore score = scoreRepository
            .findByEntityId(entityId, entityType)
            .orElse(FraudRiskScore.builder()
                .id(UUID.randomUUID())
                .entityId(entityId)
                .entityType(EntityType.fromValue(entityType))
                .gpsAnomalyScore(0)
                .duplicateVoucherScore(0)
                .exifMismatchScore(0)
                .suspiciousActivityScore(0)
                .createdAt(LocalDateTime.now())
                .build());

        score.setGpsAnomalyScore(score.getGpsAnomalyScore() + gpsAdd);
        score.setDuplicateVoucherScore(score.getDuplicateVoucherScore() + dupAdd);
        score.setExifMismatchScore(score.getExifMismatchScore() + exifAdd);
        score.setSuspiciousActivityScore(score.getSuspiciousActivityScore() + suspAdd);
        score.recalculate();
        scoreRepository.save(score);
    }
}
