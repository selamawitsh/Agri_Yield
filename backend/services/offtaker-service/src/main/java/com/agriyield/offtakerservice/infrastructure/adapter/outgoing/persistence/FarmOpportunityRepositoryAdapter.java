package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.offtakerservice.application.port.outgoing.FarmOpportunityRepositoryPort;
import com.agriyield.offtakerservice.domain.model.FarmOpportunity;
import com.agriyield.offtakerservice.infrastructure.adapter.outgoing.persistence.entity.FarmOpportunityEntity;
import com.agriyield.offtakerservice.infrastructure.repository.JpaFarmOpportunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FarmOpportunityRepositoryAdapter implements FarmOpportunityRepositoryPort {

    private final JpaFarmOpportunityRepository jpaRepository;

    @Override
    public FarmOpportunity save(FarmOpportunity o) {
        return toDomain(jpaRepository.save(toEntity(o)));
    }

    @Override
    public Optional<FarmOpportunity> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmId(farmId).map(this::toDomain);
    }

    @Override
    public List<FarmOpportunity> findAll() {
        return jpaRepository.findAllByOrderByLastUpdatedDesc()
                .stream().map(this::toDomain).toList();
    }

    /**
     * FIX: Implements all SRS §6.4 filter parameters.
     * Previous signature only had: cropType, region, harvestReady.
     * Added: minNdvi, harvestDateFrom, harvestDateTo, minYieldQuintals.
     * All filtering done in Java to avoid Hibernate null-as-bytea issues with JPQL.
     */
    @Override
    public List<FarmOpportunity> search(
            String cropType,
            String region,
            Boolean harvestReady,
            Double minNdvi,
            String harvestDateFrom,
            String harvestDateTo,
            Double minYieldQuintals) {

        Stream<FarmOpportunityEntity> stream = jpaRepository
                .findAllByOrderByLastUpdatedDesc().stream();

        if (cropType != null && !cropType.isBlank()) {
            stream = stream.filter(f -> cropType.equalsIgnoreCase(f.getCropType()));
        }
        if (region != null && !region.isBlank()) {
            String regionLower = region.toLowerCase();
            stream = stream.filter(f -> f.getRegion() != null &&
                    f.getRegion().toLowerCase().contains(regionLower));
        }
        if (harvestReady != null) {
            stream = stream.filter(f -> f.isHarvestReady() == harvestReady);
        }
        if (minNdvi != null) {
            stream = stream.filter(f -> f.getCurrentNdvi() != null &&
                    f.getCurrentNdvi().doubleValue() >= minNdvi);
        }
        if (harvestDateFrom != null && !harvestDateFrom.isBlank()) {
            stream = stream.filter(f -> f.getEstimatedHarvestDateFrom() != null &&
                    f.getEstimatedHarvestDateFrom().compareTo(harvestDateFrom) >= 0);
        }
        if (harvestDateTo != null && !harvestDateTo.isBlank()) {
            stream = stream.filter(f -> f.getEstimatedHarvestDateTo() != null &&
                    f.getEstimatedHarvestDateTo().compareTo(harvestDateTo) <= 0);
        }
        if (minYieldQuintals != null) {
            stream = stream.filter(f -> f.getPredictedYieldMeanQuintals() != null &&
                    f.getPredictedYieldMeanQuintals().doubleValue() >= minYieldQuintals);
        }

        return stream.map(this::toDomain).toList();
    }

    private FarmOpportunity toDomain(FarmOpportunityEntity e) {
        return FarmOpportunity.builder()
                .id(e.getId())
                .farmId(e.getFarmId())
                .farmerId(e.getFarmerId())
                .cropType(e.getCropType())
                .areaHectares(e.getAreaHectares())
                .region(e.getRegion())
                .kebeleCode(e.getKebeleCode())
                .gpsCentroidLat(e.getGpsCentroidLat())
                .gpsCentroidLng(e.getGpsCentroidLng())
                .agriScore(e.getAgriScore())
                .cropCycleId(e.getCropCycleId())
                .cropCycleStatus(e.getCropCycleStatus())
                .currentNdvi(e.getCurrentNdvi())
                .ndviHealthStatus(e.getNdviHealthStatus())
                .predictedYieldMinQuintals(e.getPredictedYieldMinQuintals())
                .predictedYieldMaxQuintals(e.getPredictedYieldMaxQuintals())
                .predictedYieldMeanQuintals(e.getPredictedYieldMeanQuintals())
                .yieldConfidencePct(e.getYieldConfidencePct())
                .harvestReady(e.isHarvestReady())
                .estimatedHarvestDateFrom(e.getEstimatedHarvestDateFrom())
                .estimatedHarvestDateTo(e.getEstimatedHarvestDateTo())
                .existingBidsCount(e.getExistingBidsCount())
                .lastUpdated(e.getLastUpdated())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private FarmOpportunityEntity toEntity(FarmOpportunity o) {
        return FarmOpportunityEntity.builder()
                .id(o.getId())
                .farmId(o.getFarmId())
                .farmerId(o.getFarmerId())
                .cropType(o.getCropType())
                .areaHectares(o.getAreaHectares())
                .region(o.getRegion())
                .kebeleCode(o.getKebeleCode())
                .gpsCentroidLat(o.getGpsCentroidLat())
                .gpsCentroidLng(o.getGpsCentroidLng())
                .agriScore(o.getAgriScore())
                .cropCycleId(o.getCropCycleId())
                .cropCycleStatus(o.getCropCycleStatus())
                .currentNdvi(o.getCurrentNdvi())
                .ndviHealthStatus(o.getNdviHealthStatus())
                .predictedYieldMinQuintals(o.getPredictedYieldMinQuintals())
                .predictedYieldMaxQuintals(o.getPredictedYieldMaxQuintals())
                .predictedYieldMeanQuintals(o.getPredictedYieldMeanQuintals())
                .yieldConfidencePct(o.getYieldConfidencePct())
                .harvestReady(o.isHarvestReady())
                .estimatedHarvestDateFrom(o.getEstimatedHarvestDateFrom())
                .estimatedHarvestDateTo(o.getEstimatedHarvestDateTo())
                .existingBidsCount(o.getExistingBidsCount())
                .createdAt(o.getCreatedAt() != null ? o.getCreatedAt() : OffsetDateTime.now())
                .build();
    }
}