package com.agriyield.voucherservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vouchers")
public class VoucherEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "voucher_code", nullable = false, unique = true, length = 30)
    private String voucherCode;

    @Column(name = "investment_id", nullable = false)
    private UUID investmentId;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "input_need_id", nullable = false)
    private UUID inputNeedId;

    @Column(name = "input_need_item_id", nullable = false)
    private UUID inputNeedItemId;

    @Column(name = "crop_cycle_id", nullable = false)
    private UUID cropCycleId;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "product_category", nullable = false, length = 30)
    private String productCategory;

    @Column(name = "amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountEtb;

    @Column(name = "sequence_order", nullable = false)
    private int sequenceOrder;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
