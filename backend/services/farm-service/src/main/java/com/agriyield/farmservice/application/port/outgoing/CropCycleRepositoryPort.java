package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.CropCycle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CropCycleRepositoryPort {

    CropCycle save(CropCycle cropCycle);

    Optional<CropCycle> findById(UUID cropCycleId);

    Optional<CropCycle> findActiveByFarmId(UUID farmId);

    List<CropCycle> findAllByFarmId(UUID farmId);
}
