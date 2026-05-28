package com.agriyield.weatherservice.presentation.dto.response;

import com.agriyield.weatherservice.domain.model.DroughtCondition;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class DroughtStatusResponse {
    private UUID farmId;
    private int consecutiveDryDays;
    private int droughtThresholdDays;
    private boolean isTriggered;
    private OffsetDateTime triggeredAt;
    private OffsetDateTime lastChecked;

    public static DroughtStatusResponse from(DroughtCondition domain) {
        return DroughtStatusResponse.builder()
                .farmId(domain.getFarmId())
                .consecutiveDryDays(domain.getConsecutiveDryDays())
                .droughtThresholdDays(domain.getDroughtThresholdDays())
                .isTriggered(domain.isTriggered())
                .triggeredAt(domain.getTriggeredAt())
                .lastChecked(domain.getLastChecked())
                .build();
    }
}
