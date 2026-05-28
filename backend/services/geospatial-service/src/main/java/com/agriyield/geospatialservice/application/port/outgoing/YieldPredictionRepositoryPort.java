package com.agriyield.geospatialservice.application.port.outgoing;

import com.agriyield.geospatialservice.domain.model.YieldPrediction;

import java.util.Optional;
import java.util.UUID;

public interface YieldPredictionRepositoryPort {

    YieldPrediction save(YieldPrediction prediction);

    Optional<YieldPrediction> findLatestByFarmId(UUID farmId);
}
