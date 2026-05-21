package com.agriyield.farmservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface FraudServicePort {

    // SRS Page 47 — VerifyGpsConsistency
    // Check if photo GPS falls within the farm polygon (500m tolerance)
    GpsVerificationResult verifyGpsConsistency(UUID farmId,
                                                BigDecimal photoLat,
                                                BigDecimal photoLng,
                                                String farmGeoJsonPolygon);

    record GpsVerificationResult(
            boolean isConsistent,
            double distanceFromBoundaryMeters
    ) {}
}
