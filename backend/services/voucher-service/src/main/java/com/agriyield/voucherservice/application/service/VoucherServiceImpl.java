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

    // Quantity to deduct per voucher — derived from voucher amount / regional price.
    // For now we use a default of 1 unit per voucher (configurable).
    // When per-item vouchers are built, use the input_need_item quantity.
    @Value("${app.voucher.default-deduct-quantity:1}")
    private double defaultDeductQuantity;

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
            try { funded = investmentServicePort.verifyInvestmentFunded(investmentId); }
            catch (Exception e) { log.warn("verifyInvestmentFunded failed: {}", e.getMessage()); }
            if (!funded) throw new BusinessException(
                "Investment is not funded", "INVESTMENT_NOT_FUNDED");
        }
        BigDecimal amountEtb = BigDecimal.valueOf(1000.00);
        try {
            InvestmentServicePort.InvestmentContext ctx =
                investmentServicePort.getInvestmentById(investmentId);
            amountEtb = BigDecimal.valueOf(ctx.amountEtb());
        } catch (Exception e) {
            log.warn("getInvestmentById failed — using fallback 1000 ETB: {}", e.getMessage());
        }
        Voucher voucher = Voucher.builder()
            .id(UUID.randomUUID()).voucherCode(generateCode())
            .investmentId(investmentId).farmId(farmId).farmerId(farmerId)
            .inputNeedId(inputNeedId).inputNeedItemId(inputNeedId)
            .cropCycleId(cropCycleId)
            .productName("Agricultural Input Package")
            .productCategory(ProductCategory.OTHER)
            .amountEtb(amountEtb).sequenceOrder(1)
            .status(VoucherStatus.ACTIVE)
            .issuedAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(expiryDays))
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();
        Voucher saved = voucherRepository.save(voucher);
        log.info("Generated voucher id={} code={} for investment={}",
            saved.getId(), saved.getVoucherCode(), investmentId);
        eventPublisher.publishVouchersGenerated(List.of(saved));
        return List.of(saved);
    }

    @Override @Transactional(readOnly = true)
    public List<Voucher> getMyVouchers(UUID farmerId) {
        return voucherRepository.findByFarmerId(farmerId);
    }

    @Override @Transactional(readOnly = true)
    public Voucher getById(UUID voucherId) {
        return voucherRepository.findById(voucherId)
            .orElseThrow(() -> new VoucherNotFoundException(voucherId.toString()));
    }

    @Override @Transactional(readOnly = true)
    public Voucher getByCode(String voucherCode) {
        return voucherRepository.findByVoucherCode(voucherCode)
            .orElseThrow(() -> new VoucherNotFoundException("code:" + voucherCode));
    }

    /**
     * SRS §3.5.3 — Six-step validation pipeline + inventory check.
     */
    @Override
    @Transactional
    public VoucherRedemption redeem(String voucherCode, UUID merchantId,
                                     UUID redeemedBy, String notes) {
        log.info("=== REDEMPTION START: code={} merchant={} ===", voucherCode, merchantId);

        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
            .orElseThrow(() -> new VoucherNotFoundException("code:" + voucherCode));

        // ── Check #1: Signature (skipped — alphanumeric code mode) ────────────
        log.info("Check #1 SIGNATURE — skipped (alphanumeric code mode)");

        // ── Check #2: Duplicate scan ──────────────────────────────────────────
        log.info("Check #2 DUPLICATE — checking Redis for: {}", voucherCode);
        if (duplicateScanCache.isDuplicate(voucherCode)) {
            String firstScan = duplicateScanCache.getFirstScanTimestamp(voucherCode);
            log.warn("Check #2 FAILED — DUPLICATE_SCAN. First scan: {}", firstScan);
            rejectVoucher(voucher, merchantId, "DUPLICATE_SCAN");
            throw new BusinessException(
                "Duplicate scan. Voucher already scanned at: " + firstScan, "DUPLICATE_SCAN");
        }
        log.info("Check #2 PASSED");

        // ── Check #3: Category match ──────────────────────────────────────────
        log.info("Check #3 CATEGORY — voucher: {}", voucher.getProductCategory());
        try {
            List<String> cats = merchantServicePort.getMerchantCategories(merchantId);
            String cat = voucher.getProductCategory().name();
            boolean ok = cat.equals("OTHER") || cats.isEmpty() || cats.contains(cat);
            if (!ok) {
                log.warn("Check #3 FAILED — CATEGORY_MISMATCH. Voucher: {} Merchant: {}", cat, cats);
                rejectVoucher(voucher, merchantId, "CATEGORY_MISMATCH");
                throw new BusinessException(
                    "Merchant not certified for: " + cat + ". Has: " + cats, "CATEGORY_MISMATCH");
            }
            log.info("Check #3 PASSED — category {} accepted", cat);
        } catch (BusinessException e) { throw e; }
        catch (Exception e) { log.warn("Check #3 gRPC failed — allowing: {}", e.getMessage()); }

        // ── Check #3b: INVENTORY CHECK (new feature) ──────────────────────────
        log.info("Check #3b INVENTORY — merchant={} category={}",
            merchantId, voucher.getProductCategory().name());
        try {
            MerchantServicePort.InventoryCheckResult inv = merchantServicePort.checkInventory(
                merchantId,
                voucher.getProductCategory().name(),
                defaultDeductQuantity);

            if (!inv.available()) {
                String errorCode = inv.errorCode() != null ? inv.errorCode() : "INSUFFICIENT_STOCK";
                log.warn("Check #3b FAILED — {} : {}", errorCode, inv.errorMessage());
                rejectVoucher(voucher, merchantId, errorCode);
                throw new BusinessException(inv.errorMessage(), errorCode);
            }
            log.info("Check #3b PASSED — {} {} of {} available",
                inv.availableQuantity(), inv.unit(), inv.productName());
        } catch (BusinessException e) { throw e; }
        catch (Exception e) {
            log.warn("Check #3b inventory gRPC failed — allowing redemption: {}", e.getMessage());
        }

        // ── Check #4: GPS proximity ───────────────────────────────────────────
        if (!skipGpsCheck) {
            log.info("Check #4 GPS — farm: {}", voucher.getFarmId());
            try {
                double[] merchantGps = merchantServicePort.getMerchantLocation(merchantId);
                double[] farmGps     = farmServicePort.getFarmGps(voucher.getFarmId());
                double dist = haversineDistanceKm(
                    merchantGps[0], merchantGps[1], farmGps[0], farmGps[1]);
                if (dist > maxMerchantDistanceKm) {
                    log.warn("Check #4 FAILED — MERCHANT_TOO_FAR: {} km", dist);
                    rejectVoucher(voucher, merchantId, "MERCHANT_TOO_FAR");
                    throw new BusinessException(
                        "Merchant is " + String.format("%.1f", dist) + " km from farm. Max: "
                        + maxMerchantDistanceKm + " km", "MERCHANT_TOO_FAR");
                }
                log.info("Check #4 PASSED — {} km", dist);
            } catch (BusinessException e) { throw e; }
            catch (Exception e) { log.warn("Check #4 GPS failed — allowing: {}", e.getMessage()); }
        } else {
            log.info("Check #4 GPS — skipped (skip-gps-check=true)");
        }

        // ── Check #5: Validity period ─────────────────────────────────────────
        log.info("Check #5 VALIDITY — expires: {}", voucher.getExpiresAt());
        if (LocalDateTime.now().isAfter(voucher.getExpiresAt())) {
            log.warn("Check #5 FAILED — VOUCHER_EXPIRED");
            rejectVoucher(voucher, merchantId, "VOUCHER_EXPIRED");
            throw new BusinessException("Voucher expired at: " + voucher.getExpiresAt(),
                "VOUCHER_EXPIRED");
        }
        log.info("Check #5 PASSED");

        // ── Check #6: Sequential unlock ───────────────────────────────────────
        log.info("Check #6 SEQUENCE — order: {}", voucher.getSequenceOrder());
        if (voucher.getSequenceOrder() > 1) {
            List<Voucher> notRedeemed = voucherRepository.findByFarmId(voucher.getFarmId())
                .stream()
                .filter(v -> v.getSequenceOrder() < voucher.getSequenceOrder()
                    && v.getStatus() != VoucherStatus.REDEEMED)
                .toList();
            if (!notRedeemed.isEmpty()) {
                int blockedBy = notRedeemed.stream()
                    .mapToInt(Voucher::getSequenceOrder).min().orElse(0);
                log.warn("Check #6 FAILED — blocked by #{}", blockedBy);
                rejectVoucher(voucher, merchantId, "PRECEDING_VOUCHER_NOT_REDEEMED");
                throw new BusinessException(
                    "Voucher #" + blockedBy + " must be redeemed first",
                    "PRECEDING_VOUCHER_NOT_REDEEMED");
            }
        }
        log.info("Check #6 PASSED");

        if (!voucher.isValid()) {
            rejectVoucher(voucher, merchantId, "INVALID_VOUCHER");
            throw new BusinessException(
                "Voucher not valid. Status: " + voucher.getStatus().getValue(), "INVALID_VOUCHER");
        }

        // ── All checks passed ─────────────────────────────────────────────────
        log.info("=== ALL CHECKS PASSED — processing redemption ===");

        duplicateScanCache.markScanned(voucherCode);
        voucher.redeem(merchantId);
        Voucher saved = voucherRepository.save(voucher);
        unlockNextVoucher(saved);

        VoucherRedemption redemption = VoucherRedemption.builder()
            .id(UUID.randomUUID()).voucherId(saved.getId())
            .merchantId(merchantId).redeemedBy(redeemedBy)
            .amountEtb(saved.getAmountEtb()).escrowReleased(false)
            .notes(notes).redeemedAt(LocalDateTime.now())
            .build();
        VoucherRedemption savedRedemption = redemptionRepository.save(redemption);

        // Release escrow
        try {
            escrowServicePort.releasePartial(saved.getInvestmentId(), saved.getId(),
                saved.getAmountEtb(), "Voucher redeemed by merchant: " + merchantId);
            savedRedemption.setEscrowReleased(true);
            savedRedemption = redemptionRepository.save(savedRedemption);
            log.info("Escrow released: {} ETB", saved.getAmountEtb());
        } catch (Exception e) {
            log.error("Escrow release failed: {}", e.getMessage());
        }

        // ── Deduct inventory after successful redemption ──────────────────────
        try {
            merchantServicePort.deductInventory(
                merchantId,
                saved.getProductCategory().name(),
                defaultDeductQuantity);
            log.info("Inventory deducted: {} units of {} for merchant: {}",
                defaultDeductQuantity, saved.getProductCategory().name(), merchantId);
        } catch (Exception e) {
            log.warn("Inventory deduction failed (non-critical): {}", e.getMessage());
        }

        eventPublisher.publishVoucherRedeemed(saved, savedRedemption);
        log.info("=== REDEMPTION COMPLETE: {} ETB ===", saved.getAmountEtb());
        return savedRedemption;
    }

    private void rejectVoucher(Voucher voucher, UUID merchantId, String reason) {
        try { eventPublisher.publishVoucherRejected(voucher, merchantId, reason); }
        catch (Exception e) { log.warn("Failed to publish rejection event: {}", e.getMessage()); }
    }

    private void unlockNextVoucher(Voucher redeemed) {
        try {
            voucherRepository.findByFarmId(redeemed.getFarmId()).stream()
                .filter(v -> v.getSequenceOrder() == redeemed.getSequenceOrder() + 1
                    && v.getStatus() == VoucherStatus.GENERATED)
                .findFirst().ifPresent(next -> {
                    next.setStatus(VoucherStatus.ACTIVE);
                    next.setUpdatedAt(LocalDateTime.now());
                    voucherRepository.save(next);
                    log.info("Unlocked next voucher #{} id={}", next.getSequenceOrder(), next.getId());
                });
        } catch (Exception e) { log.warn("Failed to unlock next voucher: {}", e.getMessage()); }
    }

    private double haversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
            + Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    @Override @Transactional(readOnly = true)
    public List<VoucherRedemption> getRedemptions(UUID voucherId) {
        voucherRepository.findById(voucherId)
            .orElseThrow(() -> new VoucherNotFoundException(voucherId.toString()));
        return redemptionRepository.findByVoucherId(voucherId);
    }

    @Override @Transactional(readOnly = true)
    public List<Voucher> getByFarmId(UUID farmId) {
        return voucherRepository.findByFarmId(farmId);
    }

    @Override @Transactional(readOnly = true)
    public List<Voucher> getByMerchantId(UUID merchantId) {
        return voucherRepository.findByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public void expireOverdueVouchers() {
        List<Voucher> expired = voucherRepository.findExpiredActiveVouchers();
        int count = 0;
        for (Voucher v : expired) {
            try { v.expire(); voucherRepository.save(v);
                eventPublisher.publishVoucherExpired(v); count++; }
            catch (Exception e) { log.error("Expire failed {}: {}", v.getId(), e.getMessage()); }
        }
        log.info("Expired {} vouchers", count);
    }

    @Override
    @Transactional
    public void cancelForInvestment(UUID investmentId) {
        voucherRepository.findByInvestmentId(investmentId).stream()
            .filter(v -> v.getStatus() != VoucherStatus.REDEEMED)
            .forEach(v -> { v.cancel(); voucherRepository.save(v);
                eventPublisher.publishVoucherCancelled(v); });
    }

    private String generateCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return codePrefix + "-" + uuid.substring(0,4)
            + "-" + uuid.substring(4,8) + "-" + uuid.substring(8,12);
    }
}
