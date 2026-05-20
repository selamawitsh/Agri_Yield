package com.agriyield.farmservice.domain.model;

import com.agriyield.farmservice.domain.enums.CropType;
import com.agriyield.farmservice.domain.enums.FarmStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Farm {

    private UUID id;
    private UUID farmerId;
    private String farmName;
    private CropType cropType;

    // Satellite-verified area — NOT farmer claim
    private BigDecimal areaHectares;

    private FarmStatus status;
    private String kebeleCode;
    private String region;

    // Calculated from GeoJSON polygon centroid
    private BigDecimal gpsCentroidLat;
    private BigDecimal gpsCentroidLng;

    // GeoJSON polygon string stored in MongoDB
    private String geoJsonPolygon;

    private Boolean satelliteVerified;
    private LocalDateTime satelliteVerifiedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void verify() {
        this.status = FarmStatus.VERIFIED;
        this.satelliteVerified = true;
        this.satelliteVerifiedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = FarmStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void startGrowing() {
        this.status = FarmStatus.GROWING;
        this.updatedAt = LocalDateTime.now();
    }

    public void markHarvested() {
        this.status = FarmStatus.HARVESTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = FarmStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
}
