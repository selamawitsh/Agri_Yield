package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.AgriScore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgriScoreRepositoryPort {

    AgriScore save(AgriScore agriScore);

    Optional<AgriScore> findLatestByFarmerId(UUID farmerId);

    List<AgriScore> findAllByFarmerId(UUID farmerId);

    // SRS Page 23 — weighted average across all seasons
    double calculateCumulativeScore(UUID farmerId);
}
