package com.agriyield.offtakerservice.application.port.outgoing;

import com.agriyield.offtakerservice.domain.model.FarmOpportunity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmOpportunityRepositoryPort {
    FarmOpportunity save(FarmOpportunity opportunity);
    Optional<FarmOpportunity> findByFarmId(UUID farmId);
    List<FarmOpportunity> findAll();
    List<FarmOpportunity> search(String cropType, String region, Boolean harvestReady);
}
