package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.InputNeed;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InputNeedRepositoryPort {

    InputNeed save(InputNeed inputNeed);

    Optional<InputNeed> findById(UUID inputNeedId);

    Optional<InputNeed> findByFarmIdAndCropCycleId(UUID farmId, UUID cropCycleId);

    List<InputNeed> findAllByFarmId(UUID farmId);
}
