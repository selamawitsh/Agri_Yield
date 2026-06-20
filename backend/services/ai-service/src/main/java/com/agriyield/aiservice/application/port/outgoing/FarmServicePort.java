package com.agriyield.aiservice.application.port.outgoing;

public interface FarmServicePort {

    FarmData getFarmById(String farmId);

    record FarmData(
            String farmId,
            String farmerId,
            String cropType,
            String region,
            double areaHectares,
            String status,
            String kebeleCode
    ) {}
}