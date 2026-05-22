package com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity;

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
@Table(name = "escrow_releases")
public class EscrowReleaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "escrow_account_id", nullable = false)
    private UUID escrowAccountId;

    @Column(name = "voucher_id")
    private UUID voucherId;

    @Column(name = "amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountEtb;

    @Column(name = "release_reason", length = 255)
    private String releaseReason;

    @Column(name = "released_at", nullable = false)
    private LocalDateTime releasedAt;
}