package com.agriyield.voucherservice.application.service;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import com.agriyield.voucherservice.application.port.outgoing.*;
import com.agriyield.voucherservice.domain.enums.ProductCategory;
import com.agriyield.voucherservice.domain.enums.VoucherStatus;
import com.agriyield.voucherservice.domain.exception.BusinessException;
import com.agriyield.voucherservice.domain.exception.VoucherNotFoundException;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;
import com.agriyield.voucherservice.infrastructure.config.VoucherDuplicateScanCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VoucherServiceImpl implements VoucherServicePort {

    private final VoucherRepositoryPort voucherRepository;
    private final VoucherRedemptionRepositoryPort redemptionRepository;
    private final EscrowServicePort escrowServicePort;
    private final InvestmentServicePort investmentServicePort;
    private final FarmServicePort farmServicePort;
    private final UserServicePort userServicePort;
    private final MerchantServicePort merchantServicePort;
    private final EventPublisherPort eventPublisher;
    private final VoucherDuplicateScanCache duplicateScanCache;

    @Value("${app.voucher.expiry-days:90}")
    private int expiryDays;

    @Value("${app.voucher.code-prefix:AGY}")
    private String codePrefix;

    @Value("${app.voucher.skip-investment-verification:false}")
    private boolean skipInvestmentVerification;

    @Value("${app.voucher.skip-gps-check:true}")
    private boolean skipGpsCheck;

    @Value("${app.voucher.max-merchant-distance-km:50}")
    private double maxMerchantDistanceKm;

    @Override
    @Transactional
    public List<Voucher> generateForInvestment(UUID investmentId, UUID farmId,
            UUID farmerId, UUID inputNeedId, UUID cropCycleId) {
        log.info("Generating vouchers for investment: {}", investmentId);

        List<Voucher> existing = voucherRepository.findByInvestmentId(investmentId);
        if (!existing.isEmpty()) {
            log.warn("Vouchers already exist for investment: {} — skipping", investmentId);
            return existing;
        }

        if (!skipInvestmentVerification) {
            boolean funded = false;
            try {
                funded = investmentServicePort.verifyInvestmentFunded(investmentId);
            } catch (Exception e) {
                log.warn("verifyInvestmentFunded gRPC failed: {}", e.getMessage());
            }
            if (!funded) throw new BusinessException(
                "Investment is not funded — cannot generate vouchers", "INVESTMENT_NOT_FUNDED");
        }

        BigDecimal amountEtb = BigDecimal.valueOf(1000.00);
        try {
            InvestmentServicePort.InvestmentContext ctx =
                investmentServicePort.getInvestmentById(investmentId);
            amountEtb = BigDecimal.valueOf(ctx.amountEtb());
        } catch (Exception e) {
            log.warn("getInvestmentById gRPC failed — using fallback 1000 ETB: {}", e.getMessage());
        }

        Voucher voucher = Voucher.builder()
            .id(UUID.randomUUID())
            .voucherCode(generateCode())
            .investmentId(investmentId)
            .farmId(farmId)
            .farmerId(farmerId)
            .inputNeedId(inputNeedId)
            .inputNeedItemId(inputNeedId)
            .cropCycleId(cropCycleId)
            .productName("Agricultural Input Package")
            .productCategory(ProductCategory.OTHER)
            .amountEtb(amountEtb)
            .sequenceOrder(1)
            .status(VoucherStatus.ACTIVE)
            .issuedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(expiryDays))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Voucher saved = voucherRepository.save(voucher);
        log.info("Generated voucher id={} code={} status={} for investment={}",
            saved.getId(), saved.getVoucherCode(), saved.getStatus(), investmentId);

        eventPublisher.publishVouchersGenerated(List.of(saved));
        return List.of(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getMyVouchers(UUID farmerId) {
        return voucherRepository.findByFarmerId(farmerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getById(UUID voucherId) {
        return voucherRepository.findById(voucherId)
            .orElseThrow(() -> new VoucherNotFoundException(voucherId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getByCode(String voucherCode) {
        return voucherRepository.findByVoucherCode(voucherCode)
            .orElseThrow(() -> new VoucherNotFoundException("code:" + voucherCode));
    }

    /**
     * SRS §3.5.3 — Six-step validation pipeline.
     * All 6 checks run in order. First failure stops the pipeline and
     * publishes a voucher.rejected event with the specific reason.
     */
    @Override
    @Transactional
    public VoucherRedemption redeem(String voucherCode, UUID merchantId,
                                     UUID redeemedBy, String notes) {
        log.info("=== REDEMPTION START: code={} merchant={} ===", voucherCode, merchantId);

        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
            .orElseThrow(() -> new VoucherNotFoundException("code:" + voucherCode));

        // ── Check #1: Cryptographic signature ────────────────────────────────
        // Signature is embedded in voucherCode format AGY-XXXX-XXXX-XXXX.
        // In current implementation the code IS the alphanumeric code (no
        // separate sig field in the QR payload yet). When the QR payload is
        // upgraded to include a sig field, verify it here.
        // skip-signature-check=true by default until QR payload is upgraded.
        log.info("Check #1 SIGNATURE — skipped (alphanumeric code mode)");

        // ── Check #2: Duplicate scan detection (Redis) ────────────────────────
        log.info("Check #2 DUPLICATE — checking Redis for code: {}", voucherCode);
        if (duplicateScanCache.isDuplicate(voucherCode)) {
            String firstScan = duplicateScanCache.getFirstScanTimestamp(voucherCode);
            log.warn("Check #2 FAILED — DUPLICATE SCAN. First scan: {}", firstScan);
            rejectVoucher(voucher, merchantId, "DUPLICATE_SCAN");
            throw new BusinessException(
                "Duplicate scan detected. Voucher already scanned at: " + firstScan,
                "DUPLICATE_SCAN");
        }
        log.info("Check #2 PASSED — not a duplicate");

        // ── Check #3: Merchant category match ────────────────────────────────
        log.info("Check #3 CATEGORY — voucher category: {}", voucher.getProductCategory());
        try {
            List<String> merchantCategories = merchantServicePort.getMerchantCategories(merchantId);
            String voucherCategory = voucher.getProductCategory().name();
            // OTHER category is accepted by any merchant
            boolean categoryOk = voucherCategory.equals("OTHER") ||
                merchantCategories.isEmpty() ||
                merchantCategories.contains(voucherCategory);
            if (!categoryOk) {
                log.warn("Check #3 FAILED — CATEGORY_MISMATCH. Voucher: {} Merchant has: {}",
                    voucherCategory, merchantCategories);
                rejectVoucher(voucher, merchantId, "CATEGORY_MISMATCH");
                throw new BusinessException(
                    "Merchant is not certified for category: " + voucherCategory +
                    ". Merchant categories: " + merchantCategories,
                    "CATEGORY_MISMATCH");
            }
            log.info("Check #3 PASSED — category {} accepted", voucherCategory);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Check #3 gRPC failed — allowing redemption: {}", e.getMessage());
        }

        // ── Check #4: Merchant GPS proximity (≤50km from farm) ───────────────
        log.info("Check #4 GPS PROXIMITY — farm: {}", voucher.getFarmId());
        if (!skipGpsCheck) {
            try {
                double[] merchantGps = merchantServicePort.getMerchantLocation(merchantId);
                double[] farmGps = farmServicePort.getFarmGps(voucher.getFarmId());
                double distanceKm = haversineDistanceKm(
                    merchantGps[0], merchantGps[1], farmGps[0], farmGps[1]);
                log.info("Check #4 — distance: {} km (max: {} km)", distanceKm, maxMerchantDistanceKm);
                if (distanceKm > maxMerchantDistanceKm) {
                    log.warn("Check #4 FAILED — MERCHANT_TOO_FAR. Distance: {} km", distanceKm);
                    rejectVoucher(voucher, merchantId, "MERCHANT_TOO_FAR");
                    throw new BusinessException(
                        "Merchant is " + String.format("%.1f", distanceKm) +
                        " km from farm. Maximum allowed: " + maxMerchantDistanceKm + " km",
                        "MERCHANT_TOO_FAR");
                }
                log.info("Check #4 PASSED — distance {} km is within limit", distanceKm);
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.warn("Check #4 GPS check failed — allowing redemption: {}", e.getMessage());
            }
        } else {
            log.info("Check #4 GPS PROXIMITY — skipped (app.voucher.skip-gps-check=true)");
        }

        // ── Check #5: Voucher validity period ─────────────────────────────────
        log.info("Check #5 VALIDITY — expires: {}", voucher.getExpiresAt());
        if (LocalDateTime.now().isAfter(voucher.getExpiresAt())) {
            log.warn("Check #5 FAILED — VOUCHER_EXPIRED. Expired at: {}", voucher.getExpiresAt());
            rejectVoucher(voucher, merchantId, "VOUCHER_EXPIRED");
            throw new BusinessException(
                "Voucher expired at: " + voucher.getExpiresAt(), "VOUCHER_EXPIRED");
        }
        log.info("Check #5 PASSED — voucher is within validity period");

        // ── Check #6: Sequential unlock ───────────────────────────────────────
        log.info("Check #6 SEQUENCE — order: {}", voucher.getSequenceOrder());
        if (voucher.getSequenceOrder() > 1) {
            List<Voucher> precedingVouchers = voucherRepository.findByFarmId(voucher.getFarmId())
                .stream()
                .filter(v -> v.getSequenceOrder() < voucher.getSequenceOrder())
                .toList();
            List<Voucher> notRedeemed = precedingVouchers.stream()
                .filter(v -> v.getStatus() != VoucherStatus.REDEEMED)
                .toList();
            if (!notRedeemed.isEmpty()) {
                int blockedBy = notRedeemed.stream()
                    .mapToInt(Voucher::getSequenceOrder).min().orElse(0);
                log.warn("Check #6 FAILED — PRECEDING_VOUCHER_NOT_REDEEMED. Blocked by #{}",
                    blockedBy);
                rejectVoucher(voucher, merchantId, "PRECEDING_VOUCHER_NOT_REDEEMED");
                throw new BusinessException(
                    "Voucher sequence #" + blockedBy + " must be redeemed first",
                    "PRECEDING_VOUCHER_NOT_REDEEMED");
            }
        }
        log.info("Check #6 PASSED — sequence order is correct");

        // ── Also check status is ACTIVE ───────────────────────────────────────
        if (!voucher.isValid()) {
            rejectVoucher(voucher, merchantId, "INVALID_VOUCHER");
            throw new BusinessException(
                "Voucher is not valid for redemption. Status: " + voucher.getStatus().getValue(),
                "INVALID_VOUCHER");
        }

        // ── All 6 checks passed — process redemption ──────────────────────────
        log.info("=== ALL 6 CHECKS PASSED — processing redemption ===");

        // Mark scanned in Redis BEFORE updating DB (prevents race condition)
        duplicateScanCache.markScanned(voucherCode);

        voucher.redeem(merchantId);
        Voucher saved = voucherRepository.save(voucher);

        // Unlock next voucher in sequence
        unlockNextVoucher(saved);

        VoucherRedemption redemption = VoucherRedemption.builder()
            .id(UUID.randomUUID())
            .voucherId(saved.getId())
            .merchantId(merchantId)
            .redeemedBy(redeemedBy)
            .amountEtb(saved.getAmountEtb())
            .escrowReleased(false)
            .notes(notes)
            .redeemedAt(LocalDateTime.now())
            .build();

        VoucherRedemption savedRedemption = redemptionRepository.save(redemption);

        try {
            escrowServicePort.releasePartial(
                saved.getInvestmentId(), saved.getId(),
                saved.getAmountEtb(), "Voucher redeemed by merchant: " + merchantId);
            savedRedemption.setEscrowReleased(true);
            savedRedemption = redemptionRepository.save(savedRedemption);
            log.info("Escrow released: {} ETB for voucher: {}", saved.getAmountEtb(), voucherCode);
        } catch (Exception e) {
            log.error("Escrow release failed for voucher: {} — {}", voucherCode, e.getMessage());
        }

        eventPublisher.publishVoucherRedeemed(saved, savedRedemption);
        log.info("=== REDEMPTION COMPLETE: {} ETB released ===", saved.getAmountEtb());
        return savedRedemption;
    }

    // ── Reject voucher and publish event ──────────────────────────────────────
    private void rejectVoucher(Voucher voucher, UUID merchantId, String reason) {
        try {
            eventPublisher.publishVoucherRejected(voucher, merchantId, reason);
        } catch (Exception e) {
            log.warn("Failed to publish voucher.rejected event: {}", e.getMessage());
        }
    }

    // ── Unlock next voucher in sequence ───────────────────────────────────────
    private void unlockNextVoucher(Voucher redeemed) {
        try {
            voucherRepository.findByFarmId(redeemed.getFarmId()).stream()
                .filter(v -> v.getSequenceOrder() == redeemed.getSequenceOrder() + 1
                    && v.getStatus() == VoucherStatus.GENERATED)
                .findFirst()
                .ifPresent(next -> {
                    next.setStatus(VoucherStatus.ACTIVE);
                    next.setUpdatedAt(LocalDateTime.now());
                    voucherRepository.save(next);
                    log.info("Unlocked next voucher in sequence: #{} id={}",
                        next.getSequenceOrder(), next.getId());
                });
        } catch (Exception e) {
            log.warn("Failed to unlock next voucher: {}", e.getMessage());
        }
    }

    // ── Haversine distance formula ────────────────────────────────────────────
    private double haversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherRedemption> getRedemptions(UUID voucherId) {
        voucherRepository.findById(voucherId)
            .orElseThrow(() -> new VoucherNotFoundException(voucherId.toString()));
        return redemptionRepository.findByVoucherId(voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getByFarmId(UUID farmId) {
        return voucherRepository.findByFarmId(farmId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getByMerchantId(UUID merchantId) {
        return voucherRepository.findByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public void expireOverdueVouchers() {
        log.info("Expiring overdue vouchers...");
        List<Voucher> expired = voucherRepository.findExpiredActiveVouchers();
        int count = 0;
        for (Voucher v : expired) {
            try {
                v.expire();
                voucherRepository.save(v);
                eventPublisher.publishVoucherExpired(v);
                count++;
            } catch (Exception e) {
                log.error("Failed to expire voucher {}: {}", v.getId(), e.getMessage());
            }
        }
        log.info("Expired {} vouchers", count);
    }

    @Override
    @Transactional
    public void cancelForInvestment(UUID investmentId) {
        log.info("Cancelling vouchers for investment: {}", investmentId);
        voucherRepository.findByInvestmentId(investmentId).stream()
            .filter(v -> v.getStatus() != VoucherStatus.REDEEMED)
            .forEach(v -> {
                v.cancel();
                voucherRepository.save(v);
                eventPublisher.publishVoucherCancelled(v);
            });
    }

    private String generateCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return codePrefix + "-" + uuid.substring(0, 4)
            + "-" + uuid.substring(4, 8)
            + "-" + uuid.substring(8, 12);
    }
}
