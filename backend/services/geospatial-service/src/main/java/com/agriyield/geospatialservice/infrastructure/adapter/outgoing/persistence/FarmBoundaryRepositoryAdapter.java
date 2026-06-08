package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.geospatialservice.application.port.outgoing.FarmBoundaryRepositoryPort;
import com.agriyield.geospatialservice.domain.model.FarmBoundary;
import com.agriyield.geospatialservice.infrastructure.document.FarmBoundaryDocument;
import com.agriyield.geospatialservice.infrastructure.repository.MongoFarmBoundaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FarmBoundaryRepositoryAdapter implements FarmBoundaryRepositoryPort {

    private final MongoFarmBoundaryRepository repository;

    @Override
    public Optional<FarmBoundary> findByFarmId(UUID farmId) {
        return repository.findByFarmId(farmId.toString())
                .map(this::toDomain);
    }

    @Override
    public FarmBoundary save(FarmBoundary boundary) {
        String farmIdStr = boundary.getFarmId().toString();

        // Upsert — find existing or create new
        FarmBoundaryDocument doc = repository.findByFarmId(farmIdStr)
                .orElse(new FarmBoundaryDocument());

        doc.setFarmId(farmIdStr);
        doc.setGeoJsonPolygon(boundary.getGeoJsonPolygon());
        doc.setAreaSqKm(boundary.getAreaSqKm());
        doc.setCentroidLat(boundary.getCentroidLat());
        doc.setCentroidLng(boundary.getCentroidLng());
        doc.setSatelliteVerified(boundary.isSatelliteVerified());
        doc.setUpdatedAt(LocalDateTime.now());

        if (doc.getCreatedAt() == null) {
            doc.setCreatedAt(LocalDateTime.now());
        }

        // GeoJSON coordinate order: [longitude, latitude] — NOT [lat, lng]
        doc.setCentroid(new GeoJsonPoint(
                boundary.getCentroidLng(),  // X = longitude FIRST
                boundary.getCentroidLat()   // Y = latitude SECOND
        ));

        FarmBoundaryDocument saved = repository.save(doc);
        log.debug("GS: saved farm boundary farmId={}", farmIdStr);
        return toDomain(saved);
    }

    @Override
    public List<FarmBoundary> findNearby(double lat, double lng, double radiusKm) {
        GeoJsonPoint point  = new GeoJsonPoint(lng, lat);
        Distance     radius = new Distance(radiusKm, Metrics.KILOMETERS);

        return repository.findByCentroidNear(point, radius)
                .getContent()
                .stream()
                .map(r -> toDomain(r.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FarmBoundary> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private FarmBoundary toDomain(FarmBoundaryDocument doc) {
        return FarmBoundary.builder()
                .farmId(UUID.fromString(doc.getFarmId()))
                .geoJsonPolygon(doc.getGeoJsonPolygon())
                .areaSqKm(doc.getAreaSqKm())
                .centroidLat(doc.getCentroidLat())
                .centroidLng(doc.getCentroidLng())
                .satelliteVerified(doc.isSatelliteVerified())
                .createdAt(doc.getCreatedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }
}
