package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.FarmDocument;

import java.util.Optional;

public interface FarmDocumentRepositoryPort {

    // Create or update the farm's digital twin document
    FarmDocument save(FarmDocument farmDocument);

    Optional<FarmDocument> findByFarmId(String farmId);

    // Append a new NDVI reading to the farm's history
    void appendNdviReading(String farmId, FarmDocument.NdviReading reading);

    // Append a new photo record to the farm's history
    void appendPhotoRecord(String farmId, FarmDocument.PhotoRecord photoRecord);

    // Append an agronomist report
    void appendAgronomistReport(String farmId, FarmDocument.AgronomistReport report);

    // Append a weather record
    void appendWeatherRecord(String farmId, FarmDocument.WeatherRecord record);
}
