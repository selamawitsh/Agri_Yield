package com.agriyield.aiservice.infrastructure.adapter.outgoing.geospatial;

import com.agriyield.aiservice.application.port.outgoing.GeospatialServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeospatialServiceAdapter implements GeospatialServicePort {

    @Override
    public NdviData getLatestNdvi(String farmId) {
        // TODO: Implement actual NDVI retrieval logic
        log.warn("Using default NDVI data for farm: {}. Real implementation needed!", farmId);

        return new NdviData(
                0.5,                    // default NDVI value
                "2026-06-20"           // current date
        );
    }

    @Override
    public FarmContext getFarmContext(String farmId) {
        // TODO: Implement actual farm context retrieval logic
        log.warn("Using default farm context for farm: {}. Real implementation needed!", farmId);

        return new FarmContext(
                farmId,                // farmId
                0.5,                   // ndviValue
                1.0,                   // farmAreaHa (default 1 hectare)
                "WHEAT",              // cropType
                30                     // daysSincePlanting (default 30 days)
        );
    }
}