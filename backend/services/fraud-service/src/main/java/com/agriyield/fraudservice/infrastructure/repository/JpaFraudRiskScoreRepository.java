package com.agriyield.fraudservice.infrastructure.repository;

import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.entity.FraudRiskScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaFraudRiskScoreRepository extends JpaRepository<FraudRiskScoreEntity, UUID> {

    Optional<FraudRiskScoreEntity> findByEntityIdAndEntityType(UUID entityId, String entityType);
}
