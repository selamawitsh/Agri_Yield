package com.agriyield.farmservice.infrastructure.repository;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.InputNeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaInputNeedRepository extends JpaRepository<InputNeedEntity, UUID> {

    List<InputNeedEntity> findByFarmId(UUID farmId);

    Optional<InputNeedEntity> findByFarmIdAndCropCycleId(UUID farmId, UUID cropCycleId);
}
