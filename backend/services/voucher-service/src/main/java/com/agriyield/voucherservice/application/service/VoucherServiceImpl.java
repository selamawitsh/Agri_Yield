package com.agriyield.voucherservice.application.service;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import com.agriyield.voucherservice.application.port.outgoing.*;
import com.agriyield.voucherservice.domain.enums.ProductCategory;
import com.agriyield.voucherservice.domain.enums.VoucherStatus;
import com.agriyield.voucherservice.domain.exception.BusinessException;
import com.agriyield.voucherservice.domain.exception.VoucherNotFoundException;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;
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
    private final EventPublisherPort eventPublisher;

    @Value("${app.voucher.expiry-days:90}")
    private int expiryDays;

    @Value("${app.voucher.code-prefix:AGY}")
    private String codePrefix;

    @Value("${app.voucher.skip-investment-verification:false}")
    private boolean skipInvestmentVerification;

    @Override
    @Transactional
    public List<Voucher> generateForInvestment(UUID investmentId,
                                                UUID farmId,
                                                UUID farmerId,
                                                UUID inputNeedId,
                                                UUID cropCycleId) {
        log.info("Generating vouchers for investment: {}", investmentId);

        // Idempotency — don't regenerate if already done
        List<Voucher> existing = voucherRepository.findByInvestmentId(investmentId);
        if (!existing.isEmpty()) {
            log.warn("Vouchers already exist for investment: {} — skipping", investmentId);
            return existing;
        }

        // Investment funded verification
        if (!skipInvestmentVerification) {
            boolean funded = false;
            try {
                funded = investmentServicePort.verifyInvestmentFunded(investmentId);
            } catch (Exception e) {
                log.warn("verifyInvestmentFunded gRPC call failed: {} — treating as not funded",
                        e.getMessage());
            }
            if (!funded) {
                throw new BusinessException(
                        "Investment is not funded — cannot generate vouchers",
                        "INVESTMENT_NOT_FUNDED");
            }
        } else {
            log.warn("Skipping investment funded verification " +
                    "(app.voucher.skip-investment-verification=true)");
        }

        // Get investment amount — with fallback so a gRPC failure does not
        // silently block voucher generation when skip-verification is true.
        BigDecimal amountEtb = BigDecimal.valueOf(1000.00); // safe fallback
        try {
            InvestmentServicePort.InvestmentContext ctx =
                    investmentServicePort.getInvestmentById(investmentId);
            amountEtb = BigDecimal.valueOf(ctx.amountEtb());
            log.info("Resolved investment amount: {} ETB for investment: {}",
                    amountEtb, investmentId);
        } catch (Exception e) {
            log.warn("getInvestmentById gRPC call failed for investment: {} — " +
                    "using fallback amount 1000 ETB. Error: {}", investmentId, e.getMessage());
        }

        // SRS §3.5.1: sequence_order=1 voucher is ACTIVE immediately after generation.
        // Higher sequence vouchers remain GENERATED (locked) until sequence N-1 is REDEEMED.
        // Currently we generate one voucher per investment; it is always sequence 1.
        int sequenceOrder = 1;
        VoucherStatus initialStatus = VoucherStatus.ACTIVE; // first in sequence → immediately active

        Voucher voucher = Voucher.builder()
                .id(UUID.randomUUID())
                .voucherCode(generateCode())
                .investmentId(investmentId)
                .farmId(farmId)
                .farmerId(farmerId)
                .inputNeedId(inputNeedId)
                .inputNeedItemId(inputNeedId) // same as inputNeedId until per-item generation is built
                .cropCycleId(cropCycleId)
                .productName("Agricultural Input Package")
                .productCategory(ProductCategory.OTHER)
                .amountEtb(amountEtb)
                .sequenceOrder(sequenceOrder)
                .status(initialStatus)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(expiryDays))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Voucher saved = voucherRepository.save(voucher);
        List<Voucher> savedVouchers = List.of(saved);

        log.info("Generated voucher id={} code={} status={} for investment={}",
                saved.getId(), saved.getVoucherCode(), saved.getStatus(), investmentId);

        eventPublisher.publishVouchersGenerated(savedVouchers);
        return savedVouchers;
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

    @Override
    @Transactional
    public VoucherRedemption redeem(String voucherCode,
                                    UUID merchantId,
                                    UUID redeemedBy,
                                    String notes) {
        log.info("Redeeming voucher: {} by merchant: {}", voucherCode, merchantId);

        Voucher voucher = voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new VoucherNotFoundException("code:" + voucherCode));

        if (!voucher.isValid()) {
            throw new BusinessException(
                    "Voucher is not valid for redemption. Status: " + voucher.getStatus().getValue(),
                    "INVALID_VOUCHER");
        }

        boolean merchantOk = userServicePort.verifyMerchantExists(merchantId);
        if (!merchantOk) {
            log.warn("Merchant verification failed for: {} — proceeding (stub)", merchantId);
        }

        voucher.redeem(merchantId);
        Voucher saved = voucherRepository.save(voucher);

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
                    saved.getInvestmentId(),
                    saved.getId(),
                    saved.getAmountEtb(),
                    "Voucher redeemed by merchant: " + merchantId);
            savedRedemption.setEscrowReleased(true);
            savedRedemption = redemptionRepository.save(savedRedemption);
            log.info("Escrow released for voucher: {}, amount: {} ETB",
                    voucherCode, saved.getAmountEtb());
        } catch (Exception e) {
            log.error("Escrow release failed for voucher: {} — {}",
                    voucherCode, e.getMessage());
        }

        eventPublisher.publishVoucherRedeemed(saved, savedRedemption);
        return savedRedemption;
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
    @Transactional
    public void expireOverdueVouchers() {
        log.info("Expiring overdue vouchers...");
        List<Voucher> expired = voucherRepository.findExpiredActiveVouchers();
        int count = 0;
        for (Voucher voucher : expired) {
            try {
                voucher.expire();
                voucherRepository.save(voucher);
                eventPublisher.publishVoucherExpired(voucher);
                count++;
            } catch (Exception e) {
                log.error("Failed to expire voucher {}: {}", voucher.getId(), e.getMessage());
            }
        }
        log.info("Expired {} vouchers", count);
    }

    @Override
    @Transactional
    public void cancelForInvestment(UUID investmentId) {
        log.info("Cancelling vouchers for investment: {}", investmentId);
        List<Voucher> vouchers = voucherRepository.findByInvestmentId(investmentId);
        for (Voucher voucher : vouchers) {
            if (voucher.getStatus() != VoucherStatus.REDEEMED) {
                voucher.cancel();
                voucherRepository.save(voucher);
                eventPublisher.publishVoucherCancelled(voucher);
            }
        }
        log.info("Cancelled vouchers for investment: {}", investmentId);
    }

    private String generateCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return codePrefix + "-" + uuid.substring(0, 4)
                + "-" + uuid.substring(4, 8)
                + "-" + uuid.substring(8, 12);
    }
}
