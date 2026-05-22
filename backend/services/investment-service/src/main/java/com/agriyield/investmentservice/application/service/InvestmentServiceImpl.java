package com.agriyield.investmentservice.application.service;

import com.agriyield.investmentservice.application.port.incoming.InvestmentServicePort;
import com.agriyield.investmentservice.application.port.outgoing.*;
import com.agriyield.investmentservice.domain.enums.InvestmentStatus;
import com.agriyield.investmentservice.domain.exception.BusinessException;
import com.agriyield.investmentservice.domain.exception.InvestmentNotFoundException;
import com.agriyield.investmentservice.domain.model.Investment;
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
public class InvestmentServiceImpl implements InvestmentServicePort {

    private final InvestmentRepositoryPort investmentRepository;
    private final EscrowServicePort escrowServicePort;
    private final FarmServicePort farmServicePort;
    private final UserServicePort userServicePort;
    private final EventPublisherPort eventPublisher;

    @Value("${app.investment.min-amount-etb:500.00}")
    private BigDecimal minAmountEtb;

    @Value("${app.investment.max-amount-etb:5000000.00}")
    private BigDecimal maxAmountEtb;

    @Override
    @Transactional
    public Investment placeInvestment(UUID investorId,
                                      UUID farmId,
                                      UUID inputNeedId,
                                      BigDecimal amountEtb,
                                      String notes) {
        log.info("Placing investment: investor={}, farm={}, amount={} ETB",
            investorId, farmId, amountEtb);

        if (amountEtb.compareTo(minAmountEtb) < 0) {
            throw new BusinessException(
                "Minimum investment amount is " + minAmountEtb + " ETB",
                "AMOUNT_TOO_LOW");
        }
        if (amountEtb.compareTo(maxAmountEtb) > 0) {
            throw new BusinessException(
                "Maximum investment amount is " + maxAmountEtb + " ETB",
                "AMOUNT_TOO_HIGH");
        }

        boolean investorExists = userServicePort.verifyInvestorExists(investorId);
        if (!investorExists) {
            log.warn("Investor verification failed for: {} — proceeding (stub)", investorId);
        }

        FarmServicePort.FarmContext farmContext = farmServicePort.getFarmContext(farmId);

        if (!"ACTIVE".equalsIgnoreCase(farmContext.cropCycleStatus())
                && !"PLANNING".equalsIgnoreCase(farmContext.cropCycleStatus())) {
            throw new BusinessException(
                "Farm is not accepting investments at this time. " +
                "Crop cycle status: " + farmContext.cropCycleStatus(),
                "FARM_NOT_INVESTABLE");
        }

        if (investmentRepository.existsByInvestorIdAndFarmId(investorId, farmId)) {
            throw new BusinessException(
                "You have already invested in this farm for this season",
                "DUPLICATE_INVESTMENT");
        }

        Investment investment = Investment.builder()
            .id(UUID.randomUUID())
            .investorId(investorId)
            .farmId(farmId)
            .farmerId(UUID.fromString(farmContext.farmerId()))
            .inputNeedId(inputNeedId)
            .cropCycleId(UUID.fromString(farmContext.cropCycleId()))
            .amountEtb(amountEtb)
            .status(InvestmentStatus.PENDING)
            .cropType(farmContext.cropType())
            .region(farmContext.region())
            .seasonName(farmContext.seasonName())
            .expectedReturnPct(new BigDecimal("8.50"))
            .notes(notes)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Investment saved = investmentRepository.save(investment);
        log.info("Investment saved: {}", saved.getId());

        eventPublisher.publishInvestmentPlaced(saved);

        // Lock funds in escrow immediately
        try {
            escrowServicePort.createAndLock(
                saved.getId(),
                saved.getFarmerId(),
                saved.getInvestorId(),
                saved.getAmountEtb());

            saved.markEscrowLocked();
            saved = investmentRepository.save(saved);
            log.info("Escrow locked for investment: {}", saved.getId());

            eventPublisher.publishInvestmentEscrowLocked(saved);
        } catch (Exception e) {
            log.error("Escrow lock failed for investment: {} — {}", saved.getId(), e.getMessage());
            // Investment stays PENDING; a scheduled job can retry
        }

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Investment getById(UUID investmentId) {
        return investmentRepository.findById(investmentId)
            .orElseThrow(() -> new InvestmentNotFoundException(investmentId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Investment> getMyInvestments(UUID investorId) {
        return investmentRepository.findByInvestorId(investorId);
    }

    @Override
    @Transactional
    public Investment cancel(UUID investmentId, UUID investorId, String reason) {
        log.info("Cancelling investment: {} by investor: {}", investmentId, investorId);

        Investment investment = investmentRepository.findById(investmentId)
            .orElseThrow(() -> new InvestmentNotFoundException(investmentId.toString()));

        if (!investment.getInvestorId().equals(investorId)) {
            throw new BusinessException(
                "Investment does not belong to this investor", "UNAUTHORIZED");
        }

        if (investment.getStatus() == InvestmentStatus.ACTIVE) {
            throw new BusinessException(
                "Cannot cancel an active investment after farming has started",
                "CANCEL_NOT_ALLOWED");
        }

        investment.cancel(reason);
        Investment saved = investmentRepository.save(investment);

        try {
            escrowServicePort.cancel(investmentId);
        } catch (Exception e) {
            log.error("Escrow cancel failed for investment: {} — {}", investmentId, e.getMessage());
        }

        eventPublisher.publishInvestmentCancelled(saved);
        return saved;
    }
}
