package com.agriyield.aiservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.aiservice.application.port.incoming.AiServicePort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CropPhotoEventListener {

    private final AiServicePort aiServicePort;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "ai.photo-events.queue")
    public void handleCropPhotoUploaded(String message) {
        log.info("Received crop.photo.uploaded event");
        try {
            JsonNode event = objectMapper.readTree(message);
            String farmId   = event.path("farm_id").asText(null);
            String farmerId = event.path("farmer_id").asText(null);
            String photoId  = event.path("photo_id").asText(null);
            String photoUrl = event.path("photo_url").asText(null);
            Integer daysPostPlanting = event.has("days_post_planting")
                    ? event.path("days_post_planting").asInt() : null;
            Double currentNdvi = event.has("current_ndvi")
                    ? event.path("current_ndvi").asDouble() : null;

            if (farmId == null || photoUrl == null || photoUrl.isEmpty()) {
                log.warn("Skipping — missing farm_id or photo_url");
                return;
            }
            aiServicePort.diagnoseCropDisease(
                    farmId, farmerId, photoUrl, photoId,
                    "EVENT", daysPostPlanting, currentNdvi);
        } catch (Exception e) {
            log.error("Error processing crop.photo.uploaded: {}", e.getMessage(), e);
        }
    }
}
