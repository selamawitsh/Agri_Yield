package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.geospatialservice.application.port.outgoing.FarmBoundaryRepositoryPort;
import com.agriyield.geospatialservice.domain.model.FarmBoundary;
import com.agriyield.geospatialservice.infrastructure.document.FarmBoundaryDocument;
import com.agriyield.geospatialservice.infrastructure.repository.MongoFarmBoundaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FarmBoundaryRepositoryAdapter implements FarmBoundaryRepositoryPort {

    private final MongoFarmBoundaryRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * UPSERT by farmId — prevents duplicate documents.
     * If a document with this farmId already exists, reuse its _id so
     * MongoDB updates in place instead of inserting a second document.
     */
    @Override
    public FarmBoundary save(FarmBoundary b) {
        String farmIdStr = b.getFarmId() != null ? b.getFarmId().toString() : null;

        FarmBoundaryDocument doc = toDocument(b);

        if (farmIdStr != null) {
            // Look up existing document to preserve _id
            Optional<FarmBoundaryDocument> existing =
                    mongoRepository.findByFarmId(farmIdStr);

            if (existing.isPresent()) {
                // Reuse the same _id → MongoDB will UPDATE not INSERT
                doc.setId(existing.get().getId());
                log.debug("FarmBoundary upsert: updating existing doc for farmId={}", farmIdStr);
            } else {
                log.debug("FarmBoundary upsert: inserting new doc for farmId={}", farmIdStr);
            }
        }

        return toDomain(mongoRepository.save(doc));
    }

    @Override
    public Optional<FarmBoundary> findByFarmId(UUID farmId) {
        return mongoRepository.findByFarmId(farmId.toString()).map(this::toDomain);
    }

    @Override
    public List<FarmBoundary> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ── Mapping ──────────────────────────────────────────────────────────────

    private FarmBoundaryDocument toDocument(FarmBoundary b) {
        return FarmBoundaryDocument.builder()
                .farmId(b.getFarmId() != null ? b.getFarmId().toString() : null)
                .geoJsonPolygon(b.getGeoJsonPolygon())
                .areaSqKm(b.getAreaSqKm())
                .centroidLat(b.getCentroidLat())
                .centroidLng(b.getCentroidLng())
                .satelliteVerified(b.isSatelliteVerified())
                .verifiedAt(b.getVerifiedAt())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }

    private FarmBoundary toDomain(FarmBoundaryDocument d) {
        return FarmBoundary.builder()
                .farmId(d.getFarmId() != null ? UUID.fromString(d.getFarmId()) : null)
                .geoJsonPolygon(d.getGeoJsonPolygon())
                .areaSqKm(d.getAreaSqKm())
                .centroidLat(d.getCentroidLat())
                .centroidLng(d.getCentroidLng())
                .satelliteVerified(d.isSatelliteVerified())
                .verifiedAt(d.getVerifiedAt())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}