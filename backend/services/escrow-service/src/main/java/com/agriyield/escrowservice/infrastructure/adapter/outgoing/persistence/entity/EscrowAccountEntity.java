package com.agriyield.escrowservice.infrastructure.adapter.outgoing.persistence.entity;

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
@Table(name = "escrow_accounts")
public class EscrowAccountEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "investment_id", nullable = false, unique = true)
    private UUID investmentId;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "investor_id", nullable = false)
    private UUID investorId;

    @Column(name = "total_amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmountEtb;

    @Column(name = "locked_amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal lockedAmountEtb;

    @Column(name = "released_amount_etb", nullable = false, precision = 14, scale = 2)
    private BigDecimal releasedAmountEtb;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "lock_expires_at")
    private LocalDateTime lockExpiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}