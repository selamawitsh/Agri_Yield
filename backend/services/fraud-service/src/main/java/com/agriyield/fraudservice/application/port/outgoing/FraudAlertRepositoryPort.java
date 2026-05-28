package com.agriyield.fraudservice.application.port.outgoing;

import com.agriyield.fraudservice.domain.model.FraudAlert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FraudAlertRepositoryPort {

    FraudAlert save(FraudAlert alert);

    Optional<FraudAlert> findById(UUID id);

    List<FraudAlert> findAll(String severity, boolean unresolvedOnly, int page, int size);

    List<FraudAlert> findByEntityId(UUID entityId, String entityType);

    long countUnresolvedByEntityId(UUID entityId);
}
