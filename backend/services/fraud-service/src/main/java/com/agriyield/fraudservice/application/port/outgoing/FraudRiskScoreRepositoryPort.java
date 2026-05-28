package com.agriyield.fraudservice.application.port.outgoing;

import com.agriyield.fraudservice.domain.model.FraudRiskScore;

import java.util.Optional;
import java.util.UUID;

public interface FraudRiskScoreRepositoryPort {

    FraudRiskScore save(FraudRiskScore score);

    Optional<FraudRiskScore> findByEntityId(UUID entityId, String entityType);
}
