package com.agriyield.farmservice.application.port.incoming;

import com.agriyield.farmservice.domain.model.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FarmServicePort {

    Farm registerFarm(UUID farmerId,
                      String farmName,
                      String cropType,
                      String kebeleCode,
                      String region,
                      LocalDate expectedHarvestDate,
                      String geoJsonPolygon);

    FarmPhoto uploadPhoto(UUID farmId,
                          UUID farmerId,
                          MultipartFile photo,
                          String photoType);

    // cropCycleId removed — auto-fetched from active crop cycle
    InputNeed submitInputNeeds(UUID farmId,
                               UUID farmerId,
                               List<InputNeedItemRequest> items);

    Farm getFarmById(UUID farmId);

    List<Farm> getMyFarms(UUID farmerId);

    FarmDocument getDigitalTwin(UUID farmId);

    CropCycle confirmPlanting(UUID farmId,
                              UUID farmerId,
                              LocalDate plantingDate);

    AgriScore getAgriScore(UUID farmId);

    record InputNeedItemRequest(
            String productCategory,
            String productName,
            BigDecimal quantity,
            String unit,
            BigDecimal estimatedPriceEtb,
            Integer sequenceOrder
    ) {}
}
