package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "farm_photos")
public class FarmPhotoEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;

    @Column(name = "gps_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal gpsLat;

    @Column(name = "gps_lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal gpsLng;

    @Column(name = "photo_type", nullable = false, length = 20)
    private String photoType;

    @Column(name = "gps_verified", nullable = false)
    private Boolean gpsVerified;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
}
