package com.agriyield.farmservice.infrastructure.repository;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.FarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFarmRepository extends JpaRepository<FarmEntity, UUID> {

    List<FarmEntity> findByFarmerId(UUID farmerId);

    @Query("SELECT f FROM FarmEntity f WHERE f.status NOT IN " +
           "('HARVESTED', 'FAILED', 'DORMANT')")
    List<FarmEntity> findAllActive();

    // FS-11 — dynamic search with optional filters
    @Query("SELECT f FROM FarmEntity f WHERE " +
           "(:region IS NULL OR LOWER(f.region) LIKE LOWER(CONCAT('%', :region, '%'))) AND " +
           "(:cropType IS NULL OR f.cropType = :cropType) AND " +
           "(:status IS NULL OR f.status = :status)")
    List<FarmEntity> searchFarms(
        @Param("region") String region,
        @Param("cropType") String cropType,
        @Param("status") String status);
}
