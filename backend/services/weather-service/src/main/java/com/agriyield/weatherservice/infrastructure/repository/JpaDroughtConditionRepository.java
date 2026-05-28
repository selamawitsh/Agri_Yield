package com.agriyield.weatherservice.infrastructure.repository;

import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity.DroughtConditionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaDroughtConditionRepository extends JpaRepository<DroughtConditionEntity, UUID> {
    Optional<DroughtConditionEntity> findByFarmId(UUID farmId);
    List<DroughtConditionEntity> findByIsTriggeredTrue();
}
