package com.agriyield.investmentservice.infrastructure.repository;

import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.FarmJourneyEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFarmJourneyEventRepository extends JpaRepository<FarmJourneyEventEntity, UUID> {
    List<FarmJourneyEventEntity> findByFarmIdOrderByOccurredAtAsc(UUID farmId);
}
