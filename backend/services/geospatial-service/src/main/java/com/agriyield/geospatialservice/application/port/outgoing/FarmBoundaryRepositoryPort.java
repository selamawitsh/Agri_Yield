package com.agriyield.geospatialservice.application.port.outgoing;

import com.agriyield.geospatialservice.domain.model.FarmBoundary;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmBoundaryRepositoryPort {

    FarmBoundary save(FarmBoundary boundary);

    Optional<FarmBoundary> findByFarmId(UUID farmId);

    List<FarmBoundary> findAll();

    List<FarmBoundary> findNearby(double lat, double lng, double radiusKm);
}
