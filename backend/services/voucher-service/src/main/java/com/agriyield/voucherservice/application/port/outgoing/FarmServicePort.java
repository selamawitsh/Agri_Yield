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
            // inputNeedId NOT here — proto doesn't have it, and event payload provides it directly
    ) {}

    FarmContext getFarmContext(UUID farmId);
}