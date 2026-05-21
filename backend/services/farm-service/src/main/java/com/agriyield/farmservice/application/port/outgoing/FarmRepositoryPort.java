package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.Farm;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmRepositoryPort {

    Farm save(Farm farm);

    Optional<Farm> findById(UUID farmId);

    List<Farm> findByFarmerId(UUID farmerId);

    boolean existsById(UUID farmId);

    List<Farm> findAllActive();
}
