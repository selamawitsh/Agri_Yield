package com.agriyield.investmentservice.infrastructure.repository;

import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.FarmListingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaFarmListingRepository extends JpaRepository<FarmListingEntity, UUID> {

    Optional<FarmListingEntity> findByInputNeedId(UUID inputNeedId);

    @Query("SELECT f FROM FarmListingEntity f WHERE f.status IN ('OPEN','PARTIALLY_FUNDED')")
    List<FarmListingEntity> findAllOpen();

    List<FarmListingEntity> findByCropType(String cropType);

    List<FarmListingEntity> findByRegion(String region);

    List<FarmListingEntity> findByCropTypeAndRegion(String cropType, String region);

    @Query("SELECT f FROM FarmListingEntity f WHERE f.status IN ('OPEN','PARTIALLY_FUNDED') AND f.fundingDeadline < :now")
    List<FarmListingEntity> findExpiredOpenListings(LocalDateTime now);
}
