package com.agriyield.offtakerservice.domain.model;

import com.agriyield.offtakerservice.domain.enums.BidStatus;
import com.agriyield.offtakerservice.domain.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bid {
    private UUID id;
    private UUID offtakerId;
    private UUID farmId;
    private UUID cropCycleId;
    private BigDecimal quantityQuintals;
    private BigDecimal pricePerQuintalEtb;
    private BigDecimal totalValueEtb;
    private BigDecimal bidDepositEtb;
    private BidStatus status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime acceptedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public void accept() {
        if (this.status != BidStatus.PENDING) {
            throw new BusinessException("Only PENDING bids can be accepted", "INVALID_BID_STATUS");
        }
        this.status = BidStatus.ACCEPTED;
        this.acceptedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public void reject() {
        if (this.status != BidStatus.PENDING) {
            throw new BusinessException("Only PENDING bids can be rejected", "INVALID_BID_STATUS");
        }
        this.status = BidStatus.REJECTED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markContractSigned() {
        if (this.status != BidStatus.ACCEPTED) {
            throw new BusinessException("Only ACCEPTED bids can move to CONTRACT_SIGNED", "INVALID_BID_STATUS");
        }
        this.status = BidStatus.CONTRACT_SIGNED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void complete() {
        if (this.status != BidStatus.CONTRACT_SIGNED) {
            throw new BusinessException("Only CONTRACT_SIGNED bids can be completed", "INVALID_BID_STATUS");
        }
        this.status = BidStatus.COMPLETED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void expire() {
        this.status = BidStatus.EXPIRED;
        this.updatedAt = OffsetDateTime.now();
    }

    public boolean isExpired() {
        return OffsetDateTime.now().isAfter(this.expiresAt);
    }
}
