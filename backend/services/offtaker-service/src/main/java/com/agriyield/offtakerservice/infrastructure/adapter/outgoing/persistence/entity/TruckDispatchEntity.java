package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity;

import com.agriyield.offtakerservice.domain.enums.DispatchStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "truck_dispatches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TruckDispatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "agreement_id", nullable = false)
    private UUID agreementId;

    @Column(name = "driver_fayda_id", nullable = false, length = 50)
    private String driverFaydaId;

    @Column(name = "truck_count", nullable = false)
    private int truckCount;

    @Column(name = "scheduled_pickup_date", nullable = false)
    private LocalDate scheduledPickupDate;

    @Column(name = "actual_pickup_date")
    private LocalDate actualPickupDate;

    @Column(name = "driver_penalty_escrow_etb", nullable = false, precision = 10, scale = 2)
    private BigDecimal driverPenaltyEscrowEtb;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private DispatchStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
