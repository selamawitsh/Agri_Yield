package com.agriyield.fraudservice.infrastructure.repository;

import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.entity.FraudAlertEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFraudAlertRepository extends JpaRepository<FraudAlertEntity, UUID> {

    List<FraudAlertEntity> findByEntityIdAndEntityTypeOrderByCreatedAtDesc(
        UUID entityId, String entityType);

    @Query("SELECT f FROM FraudAlertEntity f WHERE " +
           "(:severity IS NULL OR f.severity = :severity) AND " +
           "(:unresolvedOnly = false OR f.resolved = false) " +
           "ORDER BY f.createdAt DESC")
    List<FraudAlertEntity> findWithFilters(
        @Param("severity") String severity,
        @Param("unresolvedOnly") boolean unresolvedOnly,
        Pageable pageable);

    long countByEntityIdAndResolvedFalse(UUID entityId);
}
