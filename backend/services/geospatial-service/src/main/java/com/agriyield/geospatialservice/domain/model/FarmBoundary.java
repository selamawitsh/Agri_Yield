package com.agriyield.geospatialservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmBoundary {

    private UUID farmId;
    private String geoJsonPolygon;
    private double areaSqKm;
    private double centroidLat;
    private double centroidLng;
    private boolean satelliteVerified;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * SRS §9.3: Ray-casting point-in-polygon algorithm.
     * Returns true if (lat, lng) is inside this farm's GeoJSON polygon.
     * Coordinates are stored as [[lng, lat], ...] per GeoJSON spec.
     */
    public static boolean isPointInPolygon(double lat, double lng,
                                            List<double[]> polygonCoords) {
        int n = polygonCoords.size();
        boolean inside = false;
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygonCoords.get(i)[1]; // lat
            double yi = polygonCoords.get(i)[0]; // lng
            double xj = polygonCoords.get(j)[1];
            double yj = polygonCoords.get(j)[0];
            boolean intersect = ((yi > lng) != (yj > lng))
                && (lat < (xj - xi) * (lng - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }

    /**
     * Haversine distance in km between two GPS points.
     */
    public static double distanceKm(double lat1, double lon1,
                                     double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
