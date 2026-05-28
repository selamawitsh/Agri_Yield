package com.agriyield.weatherservice.application.port.outgoing;

import com.agriyield.weatherservice.domain.model.DroughtCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DroughtConditionRepositoryPort {
    DroughtCondition save(DroughtCondition condition);
    Optional<DroughtCondition> findByFarmId(UUID farmId);
    List<DroughtCondition> findAllTriggered();
    DroughtCondition findOrCreateByFarmId(UUID farmId);
}
