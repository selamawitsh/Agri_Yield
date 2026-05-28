package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.geospatialservice.application.port.outgoing.NdviReadingRepositoryPort;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.infrastructure.document.NdviReadingDocument;
import com.agriyield.geospatialservice.infrastructure.repository.MongoNdviRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NdviReadingRepositoryAdapter implements NdviReadingRepositoryPort {

    private final MongoNdviRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public NdviReading save(NdviReading reading) {
        NdviReadingDocument doc = toDocument(reading);
        return toDomain(mongoRepository.save(doc));
    }

    @Override
    public Optional<NdviReading> findLatestByFarmId(UUID farmId) {
        return mongoRepository
            .findFirstByFarmIdOrderByRecordedDateDesc(farmId.toString())
            .map(this::toDomain);
    }

    @Override
    public List<NdviReading> findByFarmIdSince(UUID farmId, LocalDate since) {
        return mongoRepository
            .findByFarmIdAndRecordedDateAfterOrderByRecordedDateAsc(
                farmId.toString(), since)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<NdviReading> findByFarmIdOrderByDateDesc(UUID farmId, int limit) {
        Query query = new Query(Criteria.where("farmId").is(farmId.toString()))
            .with(Sort.by(Sort.Direction.DESC, "recordedDate"))
            .limit(limit);
        return mongoTemplate.find(query, NdviReadingDocument.class)
            .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<NdviReading> findPeakNdviForFarm(UUID farmId) {
        return mongoRepository
            .findFirstByFarmIdOrderByNdviValueDesc(farmId.toString())
            .map(this::toDomain);
    }

    @Override
    public List<UUID> findAllActiveFarmIds() {
        // Return distinct farm IDs that have NDVI readings
        Query query = new Query();
        query.fields().include("farmId");
        return mongoTemplate.find(query, NdviReadingDocument.class)
            .stream()
            .map(d -> d.getFarmId())
            .filter(id -> id != null)
            .distinct()
            .map(UUID::fromString)
            .collect(Collectors.toList());
    }

    private NdviReadingDocument toDocument(NdviReading r) {
        return NdviReadingDocument.builder()
            .id(r.getId() != null ? r.getId().toString() : null)
            .farmId(r.getFarmId() != null ? r.getFarmId().toString() : null)
            .ndviValue(r.getNdviValue())
            .cloudCoverage(r.getCloudCoverage())
            .sentinelSceneId(r.getSentinelSceneId())
            .recordedDate(r.getRecordedDate())
            .createdAt(r.getCreatedAt())
            .build();
    }

    private NdviReading toDomain(NdviReadingDocument d) {
        return NdviReading.builder()
            .id(d.getId() != null ? UUID.fromString(d.getId()) : null)
            .farmId(d.getFarmId() != null ? UUID.fromString(d.getFarmId()) : null)
            .ndviValue(d.getNdviValue())
            .cloudCoverage(d.getCloudCoverage())
            .sentinelSceneId(d.getSentinelSceneId())
            .recordedDate(d.getRecordedDate())
            .createdAt(d.getCreatedAt())
            .build();
    }
}
