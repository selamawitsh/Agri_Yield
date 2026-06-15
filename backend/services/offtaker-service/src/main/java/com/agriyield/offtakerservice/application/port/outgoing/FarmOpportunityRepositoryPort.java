package com.agriyield.offtakerservice.application.port.outgoing;

import com.agriyield.offtakerservice.domain.model.FarmOpportunity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FarmOpportunityRepositoryPort {
    FarmOpportunity save(FarmOpportunity opportunity);
    Optional<FarmOpportunity> findByFarmId(UUID farmId);
    List<FarmOpportunity> findAll();

    /**
     * FIX: Added all SRS §6.4 filter params.
     * Previous signature: search(String cropType, String region, Boolean harvestReady)
     * Was missing: minNdvi, harvestDateFrom, harvestDateTo, minYieldQuintals
     */
    List<FarmOpportunity> search(
            String cropType,
            String region,
            Boolean harvestReady,
            Double minNdvi,
            String harvestDateFrom,
            String harvestDateTo,
            Double minYieldQuintals
    );
}