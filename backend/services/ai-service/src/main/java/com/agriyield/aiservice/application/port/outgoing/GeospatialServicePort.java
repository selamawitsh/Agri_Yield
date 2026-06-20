package com.agriyield.aiservice.application.port.outgoing;

public interface GeospatialServicePort {

    NdviData getLatestNdvi(String farmId);

    FarmContext getFarmContext(String farmId);

    record NdviData(
            double ndviValue,
            String recordedDate
    ) {}

    record FarmContext(
            String farmId,
            double ndviValue,
            double farmAreaHa,
            String cropType,
            int daysSincePlanting
    ) {}
}