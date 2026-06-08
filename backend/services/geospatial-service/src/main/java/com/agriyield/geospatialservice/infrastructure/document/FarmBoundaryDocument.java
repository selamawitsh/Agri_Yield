package com.agriyield.geospatialservice.infrastructure.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "farm_boundaries")
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

    // 2dsphere index for spatial proximity queries
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
