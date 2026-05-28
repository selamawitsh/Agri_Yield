package com.agriyield.geospatialservice.application.port.outgoing;

import com.agriyield.geospatialservice.domain.model.NdviReading;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NdviReadingRepositoryPort {

    NdviReading save(NdviReading reading);

    Optional<NdviReading> findLatestByFarmId(UUID farmId);

    List<NdviReading> findByFarmIdSince(UUID farmId, LocalDate since);

    List<NdviReading> findByFarmIdOrderByDateDesc(UUID farmId, int limit);

    Optional<NdviReading> findPeakNdviForFarm(UUID farmId);

    List<UUID> findAllActiveFarmIds();
}
