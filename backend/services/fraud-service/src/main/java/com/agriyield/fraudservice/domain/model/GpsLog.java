package com.agriyield.fraudservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GpsLog {

    private UUID id;
    private UUID entityId;
    private String entityType;
    private double latitude;
    private double longitude;
    private String context;
    private boolean flagged;
    private String flagReason;
    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;

    /**
     * Haversine formula — distance in km between two GPS points.
     * Used for impossible movement detection (FR-05).
     */
    public static double distanceKm(double lat1, double lon1,
                                     double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
