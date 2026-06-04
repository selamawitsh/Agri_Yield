package com.agriyield.geospatialservice.infrastructure.repository;

import com.agriyield.geospatialservice.infrastructure.document.FarmBoundaryDocument;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MongoFarmBoundaryRepository
        extends MongoRepository<FarmBoundaryDocument, String> {

    Optional<FarmBoundaryDocument> findByFarmId(String farmId);

    // ── GS-12: Spatial proximity query using 2dsphere index ──────────────────
    // Replaces the old findAll() call in detectSpatialOverlap().
    // Only loads farms whose centroid is within `distance` of the given point.
    // MongoDB uses the 2dsphere index on `centroid` — no collection scan.
    //
    // Usage in FarmBoundaryRepositoryAdapter:
    //   GeoJsonPoint point = new GeoJsonPoint(lng, lat);   // lng FIRST in GeoJSON
    //   Distance radius = new Distance(1.0, Metrics.KILOMETERS);
    //   GeoResults<FarmBoundaryDocument> nearby = repo.findByCentroidNear(point, radius);
    GeoResults<FarmBoundaryDocument> findByCentroidNear(GeoJsonPoint centroid,
                                                        Distance distance);

    // ── Convenience: find all farms within a radius as a plain list ───────────
    // Used by detectSpatialOverlap when we only need the documents, not distances.
    List<FarmBoundaryDocument> findByCentroidWithin(
            org.springframework.data.mongodb.core.geo.GeoJsonPolygon polygon);
}