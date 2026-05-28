package com.agriyield.fraudservice.infrastructure.repository;

import com.agriyield.fraudservice.infrastructure.adapter.outgoing.persistence.entity.GpsLogEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaGpsLogRepository extends JpaRepository<GpsLogEntity, UUID> {

    Optional<GpsLogEntity> findFirstByEntityIdAndEntityTypeOrderByRecordedAtDesc(
        UUID entityId, String entityType);

    List<GpsLogEntity> findByEntityIdAndEntityTypeOrderByRecordedAtDesc(
        UUID entityId, String entityType, Pageable pageable);
}
