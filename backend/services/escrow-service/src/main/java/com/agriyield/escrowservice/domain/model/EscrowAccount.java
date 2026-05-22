package com.agriyield.escrowservice.domain.model;

import com.agriyield.escrowservice.domain.enums.EscrowStatus;
import com.agriyield.escrowservice.domain.exception.BusinessException;
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
public class EscrowAccount {

    private UUID id;
    private UUID investmentId;
    private UUID farmerId;
    private UUID investorId;

    private BigDecimal totalAmountEtb;
    private BigDecimal lockedAmountEtb;
    private BigDecimal releasedAmountEtb;

    private EscrowStatus status;
    private LocalDateTime lockExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Called when investor funds are deposited and locked for a farm input need. */
    public void lock(LocalDateTime expiresAt) {
        if (this.status != EscrowStatus.PENDING) {
            throw new BusinessException(
                    "Only PENDING escrow accounts can be locked", "INVALID_ESCROW_STATE");
        }
        this.lockedAmountEtb = this.totalAmountEtb;
        this.status = EscrowStatus.LOCKED;
        this.lockExpiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
    }

    /** Releases a partial amount to the farmer (e.g. per voucher batch). */
    public void releasePartial(BigDecimal amount) {
        if (this.status != EscrowStatus.LOCKED && this.status != EscrowStatus.PARTIALLY_RELEASED) {
            throw new BusinessException(
                    "Only LOCKED or PARTIALLY_RELEASED accounts can release funds",
                    "INVALID_ESCROW_STATE");
        }
        BigDecimal remaining = this.lockedAmountEtb.subtract(this.releasedAmountEtb);
        if (amount.compareTo(remaining) > 0) {
            throw new BusinessException(
                    "Release amount " + amount + " exceeds remaining locked amount " + remaining,
                    "INSUFFICIENT_ESCROW_BALANCE");
        }
        this.releasedAmountEtb = this.releasedAmountEtb.add(amount);
        if (this.releasedAmountEtb.compareTo(this.totalAmountEtb) >= 0) {
            this.status = EscrowStatus.FULLY_RELEASED;
        } else {
            this.status = EscrowStatus.PARTIALLY_RELEASED;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /** Cancels the escrow and returns funds to investor. */
    public void cancel() {
        if (this.status == EscrowStatus.FULLY_RELEASED) {
            throw new BusinessException(
                    "Cannot cancel a fully released escrow", "INVALID_ESCROW_STATE");
        }
        this.status = EscrowStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /** Marks the escrow as expired when lock window has passed. */
    public void expire() {
        if (this.status != EscrowStatus.LOCKED) {
            throw new BusinessException(
                    "Only LOCKED escrows can expire", "INVALID_ESCROW_STATE");
        }
        this.status = EscrowStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getRemainingLockedAmountEtb() {
        return this.lockedAmountEtb.subtract(this.releasedAmountEtb);
    }
}