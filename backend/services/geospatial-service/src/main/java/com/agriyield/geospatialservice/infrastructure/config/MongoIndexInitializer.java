package com.agriyield.geospatialservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

/**
 * GS-12: Creates MongoDB spatial indexes on startup.
 *
 * Why we do this in code instead of relying on @GeoSpatialIndexed alone:
 * Spring Data MongoDB only auto-creates indexes when auto-index-creation=true
 * in application.yml. For production safety we leave that OFF and create
 * indexes explicitly here so we control exactly when and what gets created.
 *
 * Indexes created:
 *   1. farm_boundaries.centroid     → 2dsphere  (enables $near, $geoWithin)
 *   2. farm_boundaries.farmId       → unique     (already existed, ensured here)
 *   3. ndvi_readings.farmId + date  → compound   (speeds up time-series queries)
 *   4. yield_predictions.farmId     → descending (speeds up findLatestByFarmId)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MongoIndexInitializer {

    private final MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void createIndexes() {
        log.info("GS-12: Creating MongoDB spatial and performance indexes...");

        try {
            createFarmBoundaryIndexes();
            createNdviReadingIndexes();
            createYieldPredictionIndexes();
            log.info("GS-12: All MongoDB indexes created/verified successfully.");
        } catch (Exception e) {
            // Index creation failure must NOT crash the service —
            // it only degrades query performance, not correctness.
            log.error("GS-12: Index creation failed (service continues): {}", e.getMessage());
        }
    }

    // ── farm_boundaries ───────────────────────────────────────────────────────

    private void createFarmBoundaryIndexes() {
        IndexOperations ops = mongoTemplate.indexOps("farm_boundaries");

        // 2dsphere index on centroid field — enables all geospatial queries
        // MUST be a 2dsphere index, not 2d, because we use GeoJSON (WGS84 coordinates)
        ops.ensureIndex(
                new GeospatialIndex("centroid")
                        .typed(org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)
                        .named("idx_centroid_2dsphere")
        );
        log.info("GS-12: 2dsphere index on farm_boundaries.centroid — OK");

        // Unique index on farmId — prevents duplicate registrations
        ops.ensureIndex(
                new Index("farmId", Sort.Direction.ASC)
                        .unique()
                        .named("idx_farmId_unique")
        );
        log.info("GS-12: Unique index on farm_boundaries.farmId — OK");
    }

    // ── ndvi_readings ─────────────────────────────────────────────────────────

    private void createNdviReadingIndexes() {
        IndexOperations ops = mongoTemplate.indexOps("ndvi_readings");

        // Compound index: farmId ASC + recordedDate DESC
        // Supports getNdviTimeSeries() and findByFarmIdOrderByDateDesc() queries
        ops.ensureIndex(
                new CompoundIndexDefinition(
                        new org.bson.Document("farmId", 1).append("recordedDate", -1)
                ).named("idx_farmId_recordedDate")
        );
        log.info("GS-12: Compound index on ndvi_readings.(farmId, recordedDate) — OK");
    }

    // ── yield_predictions ─────────────────────────────────────────────────────

    private void createYieldPredictionIndexes() {
        IndexOperations ops = mongoTemplate.indexOps("yield_predictions");

        // Index: farmId ASC + createdAt DESC
        // Supports findLatestByFarmId() — returns most recent prediction first
        ops.ensureIndex(
                new CompoundIndexDefinition(
                        new org.bson.Document("farmId", 1).append("createdAt", -1)
                ).named("idx_farmId_createdAt")
        );
        log.info("GS-12: Compound index on yield_predictions.(farmId, createdAt) — OK");
    }
}