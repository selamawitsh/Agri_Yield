package com.agriyield.voucherservice.application.port.outgoing;

import java.util.UUID;

public interface FarmServicePort {

    record FarmContext(
        String farmId,
        String farmerId,
        String cropType,
        String region,
        String cropCycleId,
        String seasonName,
        String cropCycleStatus
    ) {}

    FarmContext getFarmContext(UUID farmId);
}
