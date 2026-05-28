package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "drought_conditions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroughtConditionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "farm_id", nullable = false, unique = true)
    private UUID farmId;

    @Column(name = "consecutive_dry_days", nullable = false)
    private int consecutiveDryDays;

    @Column(name = "drought_threshold_days", nullable = false)
    private int droughtThresholdDays;

    @Column(name = "is_triggered", nullable = false)
    private boolean isTriggered;

    @Column(name = "triggered_at")
    private OffsetDateTime triggeredAt;

    @Column(name = "last_checked", nullable = false)
    private OffsetDateTime lastChecked;
}
