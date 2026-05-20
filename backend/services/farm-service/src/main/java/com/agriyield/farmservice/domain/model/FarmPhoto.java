package com.agriyield.farmservice.domain.model;

import com.agriyield.farmservice.domain.enums.PhotoType;
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
public class FarmPhoto {

    private UUID id;
    private UUID farmId;

    // MinIO object URL
    private String photoUrl;

    // GPS from photo EXIF data
    private BigDecimal gpsLat;
    private BigDecimal gpsLng;

    private PhotoType photoType;

    // Set TRUE after fraud-service confirms GPS matches farm polygon
    private Boolean gpsVerified;

    private LocalDateTime uploadedAt;

    public void markGpsVerified() {
        this.gpsVerified = true;
    }
}
