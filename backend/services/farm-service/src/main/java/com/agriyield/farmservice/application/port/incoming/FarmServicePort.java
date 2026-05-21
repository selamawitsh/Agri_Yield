package com.agriyield.farmservice.application.port.incoming;

import com.agriyield.farmservice.domain.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FarmServicePort {

    // SRS Page 22 — POST /api/v1/farms
    Farm registerFarm(UUID farmerId,
                      String farmName,
                      String cropType,
                      String kebeleCode,
                      String region,
                      LocalDate expectedHarvestDate,
                      String geoJsonPolygon);

    // SRS Page 22 — POST /api/v1/farms/{farm_id}/photos
    FarmPhoto uploadPhoto(UUID farmId,
                          UUID farmerId,
                          MultipartFile photo,
                          String photoType);

    // SRS Page 22 — POST /api/v1/farms/{farm_id}/input-needs
    InputNeed submitInputNeeds(UUID farmId,
                               UUID farmerId,
                               UUID cropCycleId,
                               List<InputNeedItemRequest> items);

    // SRS Page 22 — GET /api/v1/farms/{farm_id}
    Farm getFarmById(UUID farmId);

    // SRS Page 22 — GET /api/v1/farms/my
    List<Farm> getMyFarms(UUID farmerId);

    // SRS Page 22 — GET /api/v1/farms/{farm_id}/digital-twin
    FarmDocument getDigitalTwin(UUID farmId);

    // SRS Page 22 — POST /api/v1/farms/{farm_id}/confirm-planting
    CropCycle confirmPlanting(UUID farmId, UUID farmerId, LocalDate plantingDate);

    // SRS Page 22 — GET /api/v1/farms/{farm_id}/agri-score
    AgriScore getAgriScore(UUID farmId);

    // Inner class for input need item requests
    record InputNeedItemRequest(
            String productCategory,
            String productName,
            java.math.BigDecimal quantity,
            String unit,
            java.math.BigDecimal estimatedPriceEtb,
            Integer sequenceOrder
    ) {}
}
