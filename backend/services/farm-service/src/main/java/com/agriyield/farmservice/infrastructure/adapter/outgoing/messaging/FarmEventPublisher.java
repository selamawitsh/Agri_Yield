package com.agriyield.farmservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.farmservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.farmservice.domain.model.CropCycle;
import com.agriyield.farmservice.domain.model.Farm;
import com.agriyield.farmservice.domain.model.FarmPhoto;
import com.agriyield.farmservice.domain.model.InputNeed;
import com.agriyield.farmservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FarmEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishFarmRegistered(Farm farm) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "farm.registered");
        event.put("farm_id", farm.getId().toString());
        event.put("farmer_id", farm.getFarmerId().toString());
        event.put("crop_type", farm.getCropType().getValue());
        event.put("area_hectares", farm.getAreaHectares());
        event.put("gps_centroid_lat", farm.getGpsCentroidLat());
        event.put("gps_centroid_lng", farm.getGpsCentroidLng());
        event.put("geo_json_polygon", farm.getGeoJsonPolygon());
        event.put("kebele_code", farm.getKebeleCode());
        event.put("region", farm.getRegion());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing farm.registered event for farm: {}", farm.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FARM_EXCHANGE,
            RabbitMQConfig.FARM_REGISTERED_KEY,
            event);
    }

    @Override
    public void publishInputNeedsCreated(Farm farm, InputNeed inputNeed, String seasonName) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "input.needs.created");
        event.put("farm_id", farm.getId().toString());
        event.put("input_need_id", inputNeed.getId().toString());
        event.put("crop_cycle_id", inputNeed.getCropCycleId().toString());
        event.put("total_amount_etb", inputNeed.getTotalAmountEtb());
        event.put("crop_type", farm.getCropType() != null ? farm.getCropType().getValue() : "UNKNOWN");
        event.put("region", farm.getRegion() != null ? farm.getRegion() : "UNKNOWN");
        event.put("kebele_code", farm.getKebeleCode() != null ? farm.getKebeleCode() : "UNKNOWN");
        event.put("season_name", seasonName != null ? seasonName : "UNKNOWN");
        event.put("items", inputNeed.getItems().stream()
            .map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("category", item.getProductCategory().getValue());
                itemMap.put("product_name", item.getProductName());
                itemMap.put("amount_etb", item.getEstimatedPriceEtb());
                itemMap.put("sequence_order", item.getSequenceOrder());
                return itemMap;
            }).collect(Collectors.toList()));
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing input.needs.created event for farm: {}", farm.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FARM_EXCHANGE,
            RabbitMQConfig.INPUT_NEEDS_CREATED_KEY,
            event);
    }

    @Override
    public void publishCropPhotoUploaded(FarmPhoto photo, Farm farm) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "crop.photo.uploaded");
        event.put("farm_id", farm.getId().toString());
        event.put("photo_id", photo.getId().toString());
        event.put("photo_url", photo.getPhotoUrl());
        event.put("gps_lat", photo.getGpsLat());
        event.put("gps_lng", photo.getGpsLng());
        event.put("photo_type", photo.getPhotoType().getValue());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing crop.photo.uploaded event for farm: {}", farm.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FARM_EXCHANGE,
            RabbitMQConfig.CROP_PHOTO_UPLOADED_KEY,
            event);
    }

    // SRS Section 5.2 — farm.planted
    // Payload: farm_id, farmer_id, crop_cycle_id, season_name,
    //          planting_date, expected_harvest_date, crop_type, region, timestamp
    @Override
    public void publishFarmPlanted(Farm farm, CropCycle cropCycle) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "farm.planted");
        event.put("farm_id", farm.getId().toString());
        event.put("farmer_id", farm.getFarmerId().toString());
        event.put("crop_cycle_id", cropCycle.getId().toString());
        event.put("season_name", cropCycle.getSeasonName());
        event.put("planting_date",
            cropCycle.getPlantingDate() != null
                ? cropCycle.getPlantingDate().toString() : null);
        event.put("expected_harvest_date",
            cropCycle.getExpectedHarvestDate() != null
                ? cropCycle.getExpectedHarvestDate().toString() : null);
        event.put("crop_type",
            farm.getCropType() != null ? farm.getCropType().getValue() : "UNKNOWN");
        event.put("region", farm.getRegion());
        event.put("kebele_code", farm.getKebeleCode());
        event.put("gps_centroid_lat", farm.getGpsCentroidLat());
        event.put("gps_centroid_lng", farm.getGpsCentroidLng());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing farm.planted event for farm: {}, cycle: {}",
            farm.getId(), cropCycle.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FARM_EXCHANGE,
            RabbitMQConfig.FARM_PLANTED_KEY,
            event);
    }
}
