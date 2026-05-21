package com.agriyield.farmservice.infrastructure.repository;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.CropCycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCropCycleRepository extends JpaRepository<CropCycleEntity, UUID> {

    List<CropCycleEntity> findByFarmId(UUID farmId);

    @Query("SELECT c FROM CropCycleEntity c WHERE c.farmId = :farmId " +
           "AND c.status NOT IN ('HARVESTED', 'FAILED') ORDER BY c.createdAt DESC")
    Optional<CropCycleEntity> findActiveByFarmId(UUID farmId);
}
