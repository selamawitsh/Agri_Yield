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

    InputNeed submitInputNeeds(UUID farmId,
                               UUID farmerId,
                               List<InputNeedItemRequest> items);

    List<InputNeed> getInputNeeds(UUID farmId);

    Farm getFarmById(UUID farmId);

    List<Farm> getMyFarms(UUID farmerId);

    // FS-05 — Create new crop cycle for existing farm (new season)
    CropCycle createCropCycle(UUID farmId,
                              UUID farmerId,
                              String seasonName,
                              LocalDate expectedHarvestDate);

    // FS-05 — Get all crop cycles for a farm
    List<CropCycle> getCropCycles(UUID farmId);

    FarmDocument getDigitalTwin(UUID farmId);

    CropCycle confirmPlanting(UUID farmId,
                              UUID farmerId,
                              LocalDate plantingDate);

    AgriScore getAgriScore(UUID farmId);

    // FS-11 — Search farms by region and/or crop type
    List<Farm> searchFarms(String region,
                           String cropType,
                           String status);

    record InputNeedItemRequest(
            String productCategory,
            String productName,
            BigDecimal quantity,
            String unit,
            BigDecimal estimatedPriceEtb,
            Integer sequenceOrder
    ) {}
}
