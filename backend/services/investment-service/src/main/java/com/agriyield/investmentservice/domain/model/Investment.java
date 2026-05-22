package com.agriyield.investmentservice.domain.model;

import com.agriyield.investmentservice.domain.enums.InvestmentStatus;
import com.agriyield.investmentservice.domain.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    private UUID id;
    private UUID investorId;
    private UUID farmId;
    private UUID farmerId;
    private UUID inputNeedId;
    private UUID cropCycleId;
    private BigDecimal amountEtb;
    private InvestmentStatus status;
    private String cropType;
    private String region;
    private String seasonName;
    private BigDecimal expectedReturnPct;
    private BigDecimal actualReturnPct;
    private String notes;
    private String cancelledReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void markEscrowLocked() {
        if (this.status != InvestmentStatus.PENDING) {
            throw new BusinessException(
                "Only PENDING investments can be locked", "INVALID_STATUS");
        }
        this.status = InvestmentStatus.ESCROW_LOCKED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (this.status != InvestmentStatus.ESCROW_LOCKED) {
            throw new BusinessException(
                "Only ESCROW_LOCKED investments can be activated", "INVALID_STATUS");
        }
        this.status = InvestmentStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete(BigDecimal actualReturnPct) {
        if (this.status != InvestmentStatus.ACTIVE) {
            throw new BusinessException(
                "Only ACTIVE investments can be completed", "INVALID_STATUS");
        }
        this.status = InvestmentStatus.COMPLETED;
        this.actualReturnPct = actualReturnPct;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (this.status == InvestmentStatus.COMPLETED) {
            throw new BusinessException(
                "Cannot cancel a completed investment", "INVALID_STATUS");
        }
        this.status = InvestmentStatus.CANCELLED;
        this.cancelledReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
}
