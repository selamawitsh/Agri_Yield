package com.agriyield.investmentservice.application.port.outgoing;

import java.util.UUID;

public interface FarmServicePort {

    record FarmContext(
        String farmId,
        String farmerId,
        String cropType,
        String region,
        String kebeleCode,
        int agriScore,
        String cropCycleId,
        String seasonName,
        String cropCycleStatus
    ) {}

    FarmContext getFarmContext(UUID farmId);
}
