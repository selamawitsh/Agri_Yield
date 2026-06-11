package com.agriyield.offtakerservice.infrastructure.repository;

import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.FarmOpportunityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaFarmOpportunityRepository extends JpaRepository<FarmOpportunityEntity, UUID> {

    Optional<FarmOpportunityEntity> findByFarmId(UUID farmId);

    List<FarmOpportunityEntity> findAllByOrderByLastUpdatedDesc();

    @Query("SELECT f FROM FarmOpportunityEntity f WHERE " +
           "(:cropType IS NULL OR f.cropType = :cropType) AND " +
           "(:region   IS NULL OR LOWER(f.region) LIKE LOWER(CONCAT('%', :region, '%'))) AND " +
           "(:harvestReady IS NULL OR f.harvestReady = :harvestReady)")
    List<FarmOpportunityEntity> searchOpportunities(
            @Param("cropType")     String cropType,
            @Param("region")       String region,
            @Param("harvestReady") Boolean harvestReady);
}
