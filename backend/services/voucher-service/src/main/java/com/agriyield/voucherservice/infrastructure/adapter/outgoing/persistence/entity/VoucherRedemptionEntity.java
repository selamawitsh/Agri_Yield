package com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "voucher_redemptions")
public class VoucherRedemptionEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "voucher_id", nullable = false)
    private UUID voucherId;

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "redeemed_by", nullable = false)
    private UUID redeemedBy;

    @Column(name = "amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountEtb;

    @Column(name = "escrow_released", nullable = false)
    private Boolean escrowReleased;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt;
}
