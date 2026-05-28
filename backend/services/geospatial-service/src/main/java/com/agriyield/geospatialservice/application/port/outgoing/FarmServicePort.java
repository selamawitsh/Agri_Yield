package com.agriyield.geospatialservice.application.port.outgoing;

import java.util.UUID;

public interface FarmServicePort {

    record FarmInfo(
        String farmId,
        String farmerId,
        String cropType,
        double areaHectares,
        String status,
        String region,
        String kebeleCode,
        double gpsCentroidLat,
        double gpsCentroidLng,
        boolean satelliteVerified,
        int agriScore,
        String cropCycleId,
        String seasonName,
        String cropCycleStatus
    ) {}

    FarmInfo getFarmById(UUID farmId);

    FarmInfo getFarmContext(UUID farmId);
}
