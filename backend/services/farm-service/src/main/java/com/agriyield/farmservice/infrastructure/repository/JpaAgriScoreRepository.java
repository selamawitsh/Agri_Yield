package com.agriyield.farmservice.infrastructure.repository;

import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity.AgriScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAgriScoreRepository extends JpaRepository<AgriScoreEntity, UUID> {

    List<AgriScoreEntity> findByFarmerIdOrderByCalculatedAtDesc(UUID farmerId);

    Optional<AgriScoreEntity> findTopByFarmerIdOrderByCalculatedAtDesc(UUID farmerId);

    // SRS Page 23 — cumulative score = sum / count of completed seasons
    @Query("SELECT AVG(a.score) FROM AgriScoreEntity a WHERE a.farmerId = :farmerId")
    Double calculateAverageScore(UUID farmerId);
}
