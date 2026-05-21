package com.agriyield.farmservice.infrastructure.repository;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.FarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFarmRepository extends JpaRepository<FarmEntity, UUID> {

    List<FarmEntity> findByFarmerId(UUID farmerId);

    @Query("SELECT f FROM FarmEntity f WHERE f.status NOT IN ('HARVESTED', 'FAILED', 'DORMANT')")
    List<FarmEntity> findAllActive();
}
