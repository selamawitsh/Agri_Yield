package com.agriyield.escrowservice.application.service;

import com.agriyield.escrowservice.application.port.incoming.EscrowServicePort;
import com.agriyield.escrowservice.application.port.outgoing.EscrowAccountRepositoryPort;
import com.agriyield.escrowservice.application.port.outgoing.EscrowReleaseRepositoryPort;
import com.agriyield.escrowservice.application.port.outgoing.EscrowTransactionRepositoryPort;
import com.agriyield.escrowservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.escrowservice.domain.enums.EscrowStatus;
import com.agriyield.escrowservice.domain.enums.TransactionType;
import com.agriyield.escrowservice.domain.exception.BusinessException;
import com.agriyield.escrowservice.domain.exception.EscrowNotFoundException;
import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.domain.model.EscrowRelease;
import com.agriyield.escrowservice.domain.model.EscrowTransaction;
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
public class EscrowServiceImpl implements EscrowServicePort {

    private final EscrowAccountRepositoryPort escrowAccountRepository;
    private final EscrowTransactionRepositoryPort escrowTransactionRepository;
    private final EscrowReleaseRepositoryPort escrowReleaseRepository;
    private final EventPublisherPort eventPublisher;

    @Value("${app.escrow.lock-expiry-days:90}")
    private int lockExpiryDays;

    @Override
    @Transactional
    public EscrowAccount createAndLock(UUID investmentId,
                                       UUID farmerId,
                                       UUID investorId,
                                       BigDecimal amountEtb) {
        log.info("Creating escrow account for investment: {}, amount: {} ETB",
                investmentId, amountEtb);

        if (amountEtb == null || amountEtb.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                    "Escrow amount must be greater than zero", "INVALID_AMOUNT");
        }

        escrowAccountRepository.findByInvestmentId(investmentId).ifPresent(existing -> {
            throw new BusinessException(
                    "Escrow account already exists for investment: " + investmentId,
                    "DUPLICATE_ESCROW");
        });

        EscrowAccount escrowAccount = EscrowAccount.builder()
                .id(UUID.randomUUID())
                .investmentId(investmentId)
                .farmerId(farmerId)
                .investorId(investorId)
                .totalAmountEtb(amountEtb)
                .lockedAmountEtb(BigDecimal.ZERO)
                .releasedAmountEtb(BigDecimal.ZERO)
                .status(EscrowStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        EscrowAccount saved = escrowAccountRepository.save(escrowAccount);

        // Lock immediately — investment is already confirmed at this point
        saved.lock(LocalDateTime.now().plusDays(lockExpiryDays));
        saved = escrowAccountRepository.save(saved);

        // Ledger entry
        EscrowTransaction lockTx = EscrowTransaction.builder()
                .id(UUID.randomUUID())
                .escrowAccountId(saved.getId())
                .transactionType(TransactionType.LOCK)
                .amountEtb(amountEtb)
                .referenceId(investmentId)
                .description("Initial lock for investment " + investmentId)
                .createdAt(LocalDateTime.now())
                .build();
        escrowTransactionRepository.save(lockTx);

        log.info("Escrow locked: {} ETB for investment: {}", amountEtb, investmentId);
        eventPublisher.publishEscrowLocked(saved);

        return saved;
    }

    @Override
    @Transactional
    public EscrowRelease releasePartial(UUID investmentId,
                                        UUID voucherId,
                                        BigDecimal amountEtb,
                                        String releaseReason) {
        log.info("Releasing {} ETB from escrow for investment: {}, voucher: {}",
                amountEtb, investmentId, voucherId);

        EscrowAccount escrowAccount = escrowAccountRepository
                .findByInvestmentId(investmentId)
                .orElseThrow(() -> new EscrowNotFoundException(investmentId.toString()));

        // Domain method validates state and balance
        escrowAccount.releasePartial(amountEtb);
        EscrowAccount saved = escrowAccountRepository.save(escrowAccount);

        EscrowRelease release = EscrowRelease.builder()
                .id(UUID.randomUUID())
                .escrowAccountId(saved.getId())
                .voucherId(voucherId)
                .amountEtb(amountEtb)
                .releaseReason(releaseReason)
                .releasedAt(LocalDateTime.now())
                .build();
        escrowReleaseRepository.save(release);

        // Ledger entry
        EscrowTransaction releaseTx = EscrowTransaction.builder()
                .id(UUID.randomUUID())
                .escrowAccountId(saved.getId())
                .transactionType(saved.getStatus() == com.agriyield.escrowservice.domain.enums.EscrowStatus.FULLY_RELEASED
                        ? TransactionType.RELEASE
                        : TransactionType.PARTIAL_RELEASE)
                .amountEtb(amountEtb)
                .referenceId(voucherId)
                .description(releaseReason)
                .createdAt(LocalDateTime.now())
                .build();
        escrowTransactionRepository.save(releaseTx);

        if (saved.getStatus() == EscrowStatus.FULLY_RELEASED) {
            log.info("Escrow fully released for investment: {}", investmentId);
            eventPublisher.publishEscrowFullyReleased(saved);
        } else {
            log.info("Escrow partially released for investment: {}, remaining: {} ETB",
                    investmentId, saved.getRemainingLockedAmountEtb());
            eventPublisher.publishEscrowPartiallyReleased(saved, release);
        }

        return release;
    }

    @Override
    @Transactional
    public EscrowAccount cancel(UUID investmentId) {
        log.info("Cancelling escrow for investment: {}", investmentId);

        EscrowAccount escrowAccount = escrowAccountRepository
                .findByInvestmentId(investmentId)
                .orElseThrow(() -> new EscrowNotFoundException(investmentId.toString()));

        escrowAccount.cancel();
        EscrowAccount saved = escrowAccountRepository.save(escrowAccount);

        EscrowTransaction cancelTx = EscrowTransaction.builder()
                .id(UUID.randomUUID())
                .escrowAccountId(saved.getId())
                .transactionType(TransactionType.REFUND)
                .amountEtb(saved.getTotalAmountEtb().subtract(saved.getReleasedAmountEtb()))
                .referenceId(investmentId)
                .description("Escrow cancelled — funds returned to investor")
                .createdAt(LocalDateTime.now())
                .build();
        escrowTransactionRepository.save(cancelTx);

        eventPublisher.publishEscrowCancelled(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public EscrowAccount getByInvestmentId(UUID investmentId) {
        return escrowAccountRepository
                .findByInvestmentId(investmentId)
                .orElseThrow(() -> new EscrowNotFoundException(investmentId.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscrowTransaction> getTransactions(UUID investmentId) {
        EscrowAccount escrowAccount = escrowAccountRepository
                .findByInvestmentId(investmentId)
                .orElseThrow(() -> new EscrowNotFoundException(investmentId.toString()));
        return escrowTransactionRepository.findByEscrowAccountId(escrowAccount.getId());
    }
}