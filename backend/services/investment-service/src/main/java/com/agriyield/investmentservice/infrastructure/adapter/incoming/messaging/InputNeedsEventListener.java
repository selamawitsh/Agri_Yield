package com.agriyield.investmentservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.investmentservice.application.port.incoming.ListingServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InputNeedsEventListener {

    private final ListingServicePort listingService;

    /** IS-04: Consumes farm.exchange / input.needs.created event
     *  and automatically creates a farm listing */
    @RabbitListener(queues = "investment.input-needs.queue")
    public void onInputNeedsCreated(Map<String, Object> event) {
        log.info("IS-04: Received input.needs.created event: {}", event);
        try {
            UUID farmId       = UUID.fromString((String) event.get("farm_id"));
            UUID inputNeedId  = UUID.fromString((String) event.get("input_need_id"));
            UUID cropCycleId  = UUID.fromString((String) event.get("crop_cycle_id"));

            // total_amount_etb may come as Double or String
            BigDecimal totalAmount = new BigDecimal(event.get("total_amount_etb").toString());

            // These come from farm.registered or are passed in event payload
            // farm-service publishes these fields in the input.needs.created event
            String cropType   = (String) event.getOrDefault("crop_type", "UNKNOWN");
            String region     = (String) event.getOrDefault("region", "UNKNOWN");
            String kebeleCode = (String) event.getOrDefault("kebele_code", "UNKNOWN");
            String seasonName = (String) event.getOrDefault("season_name", "UNKNOWN");
            int agriScore     = event.containsKey("agri_score")
                ? Integer.parseInt(event.get("agri_score").toString()) : 50;

            // farmerId — derive from farm context or event
            UUID farmerId = event.containsKey("farmer_id")
                ? UUID.fromString((String) event.get("farmer_id"))
                : farmId; // fallback

            listingService.createFromInputNeeds(
                farmId, farmerId, inputNeedId, cropCycleId,
                totalAmount, cropType, region, kebeleCode, seasonName, agriScore);

            log.info("IS-04: Listing created successfully for farm: {}", farmId);
        } catch (Exception e) {
            log.error("IS-04: Failed to process input.needs.created event: {}", e.getMessage(), e);
            // Dead-letter queue will handle retries
        }
    }
}
