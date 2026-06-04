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

        // Upsert — find existing doc or create new one
        FarmBoundaryDocument existing = repository.findByFarmId(farmIdStr)
                .orElse(FarmBoundaryDocument.builder()
                        .farmId(farmIdStr)
                        .createdAt(LocalDateTime.now())
                        .build());

        existing.setGeoJsonPolygon(boundary.getGeoJsonPolygon());
        existing.setAreaSqKm(boundary.getAreaSqKm());
        existing.setCentroidLat(boundary.getCentroidLat());
        existing.setCentroidLng(boundary.getCentroidLng());
        existing.setSatelliteVerified(boundary.isSatelliteVerified());
        existing.setUpdatedAt(LocalDateTime.now());

        // ── GS-12: Store GeoJsonPoint for 2dsphere spatial queries ───────────
        // GeoJSON coordinate order is [longitude, latitude] — NOT [lat, lng].
        // This is the opposite of what most people expect. Getting this wrong
        // means all spatial queries silently return wrong results.
        existing.setCentroid(new GeoJsonPoint(
                boundary.getCentroidLng(),   // X = longitude FIRST
                boundary.getCentroidLat()    // Y = latitude SECOND
        ));

        FarmBoundaryDocument saved = repository.save(existing);
        log.debug("GS-12: Saved farm boundary with 2dsphere centroid for farmId={}",
                boundary.getFarmId());
        return toDomain(saved);
    }

    // ── GS-12: Proximity search — replaces the old findAll() ─────────────────
    // Returns only farms whose centroid is within `radiusKm` of the given point.
    // MongoDB uses the 2dsphere index — O(log n) instead of O(n).
    //
    // Used by GeospatialServiceImpl.detectSpatialOverlap() to find candidate
    // farms to check for polygon overlap, instead of loading every farm.
    @Override
    public List<FarmBoundary> findNearby(double lat, double lng, double radiusKm) {
        // GeoJSON: longitude first, latitude second
        GeoJsonPoint point   = new GeoJsonPoint(lng, lat);
        Distance     radius  = new Distance(radiusKm, Metrics.KILOMETERS);

        return repository.findByCentroidNear(point, radius)
                .getContent()
                .stream()
                .map(r -> toDomain(r.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FarmBoundary> findAll() {
        // Still available for admin use, but detectSpatialOverlap() no longer calls this.
        return repository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    // ── Mapping ───────────────────────────────────────────────────────────────

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