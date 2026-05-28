package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.geospatialservice.application.port.outgoing.YieldPredictionRepositoryPort;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;
import com.agriyield.geospatialservice.infrastructure.document.YieldPredictionDocument;
import com.agriyield.geospatialservice.infrastructure.repository.MongoYieldPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class YieldPredictionRepositoryAdapter implements YieldPredictionRepositoryPort {

    private final MongoYieldPredictionRepository mongoRepository;

    @Override
    public YieldPrediction save(YieldPrediction p) {
        return toDomain(mongoRepository.save(toDocument(p)));
    }

    @Override
    public Optional<YieldPrediction> findLatestByFarmId(UUID farmId) {
        return mongoRepository
            .findFirstByFarmIdOrderByCreatedAtDesc(farmId.toString())
            .map(this::toDomain);
    }

    private YieldPredictionDocument toDocument(YieldPrediction p) {
        return YieldPredictionDocument.builder()
            .id(p.getId() != null ? p.getId().toString() : null)
            .farmId(p.getFarmId() != null ? p.getFarmId().toString() : null)
            .cropCycleId(p.getCropCycleId() != null ? p.getCropCycleId().toString() : null)
            .cropType(p.getCropType())
            .predictedYieldMin(p.getPredictedYieldMin())
            .predictedYieldMax(p.getPredictedYieldMax())
            .predictedYieldMean(p.getPredictedYieldMean())
            .totalYieldMinQuintals(p.getTotalYieldMinQuintals())
            .totalYieldMaxQuintals(p.getTotalYieldMaxQuintals())
            .totalYieldMeanQuintals(p.getTotalYieldMeanQuintals())
            .confidencePct(p.getConfidencePct())
            .weeksToHarvest(p.getWeeksToHarvest())
            .modelVersion(p.getModelVersion())
            .ndviPeak(p.getNdviPeak())
            .ndviGrowthRate(p.getNdviGrowthRate())
            .totalRainfallMm(p.getTotalRainfallMm())
            .avgTempC(p.getAvgTempC())
            .altitudeM(p.getAltitudeM())
            .inputQuality(p.getInputQuality())
            .predictedAt(p.getPredictedAt())
            .createdAt(p.getCreatedAt())
            .build();
    }

    private YieldPrediction toDomain(YieldPredictionDocument d) {
        return YieldPrediction.builder()
            .id(d.getId() != null ? UUID.fromString(d.getId()) : null)
            .farmId(d.getFarmId() != null ? UUID.fromString(d.getFarmId()) : null)
            .cropCycleId(d.getCropCycleId() != null ? UUID.fromString(d.getCropCycleId()) : null)
            .cropType(d.getCropType())
            .predictedYieldMin(d.getPredictedYieldMin())
            .predictedYieldMax(d.getPredictedYieldMax())
            .predictedYieldMean(d.getPredictedYieldMean())
            .totalYieldMinQuintals(d.getTotalYieldMinQuintals())
            .totalYieldMaxQuintals(d.getTotalYieldMaxQuintals())
            .totalYieldMeanQuintals(d.getTotalYieldMeanQuintals())
            .confidencePct(d.getConfidencePct())
            .weeksToHarvest(d.getWeeksToHarvest())
            .modelVersion(d.getModelVersion())
            .ndviPeak(d.getNdviPeak())
            .ndviGrowthRate(d.getNdviGrowthRate())
            .totalRainfallMm(d.getTotalRainfallMm())
            .avgTempC(d.getAvgTempC())
            .altitudeM(d.getAltitudeM())
            .inputQuality(d.getInputQuality())
            .predictedAt(d.getPredictedAt())
            .createdAt(d.getCreatedAt())
            .build();
    }
}
