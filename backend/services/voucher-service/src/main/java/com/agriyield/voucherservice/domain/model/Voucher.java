package com.agriyield.voucherservice.domain.model;

import com.agriyield.voucherservice.domain.enums.ProductCategory;
import com.agriyield.voucherservice.domain.enums.VoucherStatus;
import com.agriyield.voucherservice.domain.exception.BusinessException;
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
public class Voucher {

    private UUID id;
    private String voucherCode;
    private UUID investmentId;
    private UUID farmId;
    private UUID farmerId;
    private UUID merchantId;
    private UUID inputNeedId;
    private UUID inputNeedItemId;
    private UUID cropCycleId;
    private String productName;
    private ProductCategory productCategory;
    private BigDecimal amountEtb;
    private int sequenceOrder;
    private VoucherStatus status;
    private LocalDateTime issuedAt;
    private LocalDateTime redeemedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void redeem(UUID merchantId) {
        if (this.status != VoucherStatus.ACTIVE
                && this.status != VoucherStatus.ISSUED
                && this.status != VoucherStatus.GENERATED) {
            throw new BusinessException(
                "Voucher cannot be redeemed. Status: " + this.status.getValue(),
                "INVALID_VOUCHER_STATE");
        }
        if (LocalDateTime.now().isAfter(this.expiresAt)) {
            throw new BusinessException("Voucher has expired", "VOUCHER_EXPIRED");
        }
        this.merchantId = merchantId;
        this.status = VoucherStatus.REDEEMED;
        this.redeemedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void expire() {
        if (this.status == VoucherStatus.REDEEMED || this.status == VoucherStatus.CANCELLED) {
            throw new BusinessException(
                "Cannot expire a redeemed or cancelled voucher", "INVALID_VOUCHER_STATE");
        }
        this.status = VoucherStatus.EXPIRED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status == VoucherStatus.REDEEMED) {
            throw new BusinessException(
                "Cannot cancel a redeemed voucher", "INVALID_VOUCHER_STATE");
        }
        this.status = VoucherStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isValid() {
        return (this.status == VoucherStatus.ACTIVE
                || this.status == VoucherStatus.ISSUED
                || this.status == VoucherStatus.GENERATED)
            && LocalDateTime.now().isBefore(this.expiresAt);
    }
}
