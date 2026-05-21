package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "farms")
public class FarmEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "farm_name")
    private String farmName;

    @Column(name = "crop_type", nullable = false, length = 30)
    private String cropType;

    @Column(name = "area_hectares", precision = 8, scale = 4)
    private BigDecimal areaHectares;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "kebele_code", nullable = false, length = 20)
    private String kebeleCode;

    @Column(name = "region", nullable = false, length = 50)
    private String region;

    @Column(name = "gps_centroid_lat", precision = 10, scale = 7)
    private BigDecimal gpsCentroidLat;

    @Column(name = "gps_centroid_lng", precision = 10, scale = 7)
    private BigDecimal gpsCentroidLng;

    @Column(name = "geo_json_polygon", columnDefinition = "TEXT")
    private String geoJsonPolygon;

    @Column(name = "satellite_verified", nullable = false)
    private Boolean satelliteVerified;

    @Column(name = "satellite_verified_at")
    private LocalDateTime satelliteVerifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
