package com.agriyield.farmservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface GeospatialServicePort {

    record PolygonValidation(
        boolean valid,
        String message,
        BigDecimal areaHectares,
        BigDecimal centroidLat,
        BigDecimal centroidLng
    ) {}

    record SpatialOverlap(
        boolean hasOverlap,
        String message,
        UUID conflictingFarmId
    ) {}

    record BoundaryCheck(
        boolean withinBoundary,
        double distanceMeters
    ) {}

    PolygonValidation validatePolygon(String geoJsonPolygon);

    SpatialOverlap detectSpatialOverlap(UUID farmId, String geoJsonPolygon,
                                        BigDecimal centroidLat, BigDecimal centroidLng);

    void registerFarmPolygon(UUID farmId, String geoJsonPolygon,
                             BigDecimal centroidLat, BigDecimal centroidLng,
                             BigDecimal areaHectares);
}
