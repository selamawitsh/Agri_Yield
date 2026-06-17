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
    // FIX: was @GeneratedValue(strategy = GenerationType.UUID)
    // That caused a conflict: the application layer assigned an ID via
    // UUID.randomUUID() in findOrCreateByFarmId, then Hibernate saw a
    // non-null ID and tried to merge (UPDATE). When no row existed it
    // fell back to INSERT, but a second concurrent call found the unique
    // farm_id constraint and threw DataIntegrityViolationException.
    // Solution: application layer owns the ID (set in findOrCreateByFarmId),
    // so @GeneratedValue must be removed. Hibernate will always INSERT when
    // the entity is new (no existing row) and UPDATE when it already exists.
    @Column(name = "id", updatable = false, nullable = false)
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