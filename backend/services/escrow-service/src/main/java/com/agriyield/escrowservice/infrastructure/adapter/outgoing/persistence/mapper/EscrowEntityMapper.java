package com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.escrowservice.domain.enums.EscrowStatus;
import com.agriyield.escrowservice.domain.enums.TransactionType;
import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.domain.model.EscrowRelease;
import com.agriyield.escrowservice.domain.model.EscrowTransaction;
import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity.EscrowAccountEntity;
import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity.EscrowReleaseEntity;
import com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity.EscrowTransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class EscrowEntityMapper {

    // ─── EscrowAccount ───────────────────────────────────────

    public EscrowAccount toDomain(EscrowAccountEntity entity) {
        if (entity == null) return null;
        return EscrowAccount.builder()
                .id(entity.getId())
                .investmentId(entity.getInvestmentId())
                .farmerId(entity.getFarmerId())
                .investorId(entity.getInvestorId())
                .totalAmountEtb(entity.getTotalAmountEtb())
                .lockedAmountEtb(entity.getLockedAmountEtb())
                .releasedAmountEtb(entity.getReleasedAmountEtb())
                .status(EscrowStatus.fromValue(entity.getStatus()))
                .lockExpiresAt(entity.getLockExpiresAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public EscrowAccountEntity toEntity(EscrowAccount domain) {
        if (domain == null) return null;
        return EscrowAccountEntity.builder()
                .id(domain.getId())
                .investmentId(domain.getInvestmentId())
                .farmerId(domain.getFarmerId())
                .investorId(domain.getInvestorId())
                .totalAmountEtb(domain.getTotalAmountEtb())
                .lockedAmountEtb(domain.getLockedAmountEtb())
                .releasedAmountEtb(domain.getReleasedAmountEtb())
                .status(domain.getStatus().getValue())
                .lockExpiresAt(domain.getLockExpiresAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    // ─── EscrowTransaction ───────────────────────────────────

    public EscrowTransaction toDomain(EscrowTransactionEntity entity) {
        if (entity == null) return null;
        return EscrowTransaction.builder()
                .id(entity.getId())
                .escrowAccountId(entity.getEscrowAccountId())
                .transactionType(TransactionType.fromValue(entity.getTransactionType()))
                .amountEtb(entity.getAmountEtb())
                .referenceId(entity.getReferenceId())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public EscrowTransactionEntity toEntity(EscrowTransaction domain) {
        if (domain == null) return null;
        return EscrowTransactionEntity.builder()
                .id(domain.getId())
                .escrowAccountId(domain.getEscrowAccountId())
                .transactionType(domain.getTransactionType().getValue())
                .amountEtb(domain.getAmountEtb())
                .referenceId(domain.getReferenceId())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    // ─── EscrowRelease ───────────────────────────────────────

    public EscrowRelease toDomain(EscrowReleaseEntity entity) {
        if (entity == null) return null;
        return EscrowRelease.builder()
                .id(entity.getId())
                .escrowAccountId(entity.getEscrowAccountId())
                .voucherId(entity.getVoucherId())
                .amountEtb(entity.getAmountEtb())
                .releaseReason(entity.getReleaseReason())
                .releasedAt(entity.getReleasedAt())
                .build();
    }

    public EscrowReleaseEntity toEntity(EscrowRelease domain) {
        if (domain == null) return null;
        return EscrowReleaseEntity.builder()
                .id(domain.getId())
                .escrowAccountId(domain.getEscrowAccountId())
                .voucherId(domain.getVoucherId())
                .amountEtb(domain.getAmountEtb())
                .releaseReason(domain.getReleaseReason())
                .releasedAt(domain.getReleasedAt())
                .build();
    }
}