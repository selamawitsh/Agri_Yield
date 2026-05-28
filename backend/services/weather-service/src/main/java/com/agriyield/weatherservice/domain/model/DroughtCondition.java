package com.agriyield.weatherservice.domain.model;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DroughtCondition {
    private UUID id;
    private UUID farmId;
    private int consecutiveDryDays;
    private int droughtThresholdDays;
    private boolean isTriggered;
    private OffsetDateTime triggeredAt;
    private OffsetDateTime lastChecked;

    public boolean isWarningLevel(int warningDays) {
        return consecutiveDryDays >= warningDays && !isTriggered;
    }

    public void incrementDryDay() {
        this.consecutiveDryDays++;
        this.lastChecked = OffsetDateTime.now();
        if (this.consecutiveDryDays >= this.droughtThresholdDays && !this.isTriggered) {
            this.isTriggered = true;
            this.triggeredAt = OffsetDateTime.now();
        }
    }

    public void resetDryDays() {
        this.consecutiveDryDays = 0;
        this.isTriggered = false;
        this.triggeredAt = null;
        this.lastChecked = OffsetDateTime.now();
    }
}
