package com.agriyield.geospatialservice.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "farm_boundaries")
@CompoundIndexes({
        // Speeds up detectSpatialOverlap() proximity queries
        @CompoundIndex(name = "idx_centroid_farmId", def = "{'centroid': '2dsphere', 'farmId': 1}")
})
public class FarmBoundaryDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("farmId")
    private String farmId;

    @Field("geoJsonPolygon")
    private String geoJsonPolygon;

    @Field("areaSqKm")
    private double areaSqKm;

    @Field("centroidLat")
    private double centroidLat;

    @Field("centroidLng")
    private double centroidLng;

    // ── GS-12: GeoJSON Point for MongoDB 2dsphere spatial indexing ────────────
    // Stored as { type: "Point", coordinates: [lng, lat] }
    // NOTE: GeoJSON uses [longitude, latitude] order — NOT [lat, lng].
    // This field is what enables $near, $geoWithin, and $geoIntersects queries.
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    @Field("centroid")
    private GeoJsonPoint centroid;

    @Field("satelliteVerified")
    private boolean satelliteVerified;

    @Field("verifiedAt")
    private LocalDateTime verifiedAt;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;
}