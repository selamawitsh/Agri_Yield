package com.agriyield.geospatialservice.application.port.outgoing;

import com.agriyield.geospatialservice.domain.model.NdviReading;

import java.util.UUID;

public interface CopernicusClientPort {

    /**
     * SRS §3.6.1: Fetch latest Sentinel-2 NDVI for a farm.
     * Queries Copernicus STAC API for cloud cover < 30%.
     * Returns null if no suitable scene found.
     */
    NdviReading fetchNdvi(UUID farmId,
                           double lat,
                           double lng,
                           String geoJsonPolygon);
}
