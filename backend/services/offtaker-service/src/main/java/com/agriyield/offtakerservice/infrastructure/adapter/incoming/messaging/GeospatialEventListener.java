package com.agriyield.offtakerservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.offtakerservice.application.port.outgoing.FarmOpportunityRepositoryPort;
import com.agriyield.offtakerservice.domain.model.FarmOpportunity;
import com.agriyield.offtakerservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeospatialEventListener {

    private final FarmOpportunityRepositoryPort opportunityRepository;

    // UC-OFF-12: harvest.predicted → farm becomes visible in marketplace
    @RabbitListener(queues = RabbitMQConfig.HARVEST_PREDICTED_QUEUE)
    public void onHarvestPredicted(Map<String, Object> payload) {
        String farmIdStr = (String) payload.get("farm_id");
        if (farmIdStr == null) return;

        log.info("harvest.predicted received for farmId={}", farmIdStr);
        UUID farmId = UUID.fromString(farmIdStr);

        FarmOpportunity opportunity = opportunityRepository.findByFarmId(farmId)
                .orElseGet(() -> FarmOpportunity.builder()
                        .farmId(farmId)
                        .agriScore(50)
                        .existingBidsCount(0)
                        .createdAt(OffsetDateTime.now())
                        .build());

        opportunity.setHarvestReady(true);
        opportunity.setEstimatedHarvestDateFrom(
                (String) payload.get("estimated_harvest_date_from"));
        opportunity.setEstimatedHarvestDateTo(
                (String) payload.get("estimated_harvest_date_to"));

        Object ndvi = payload.get("current_ndvi");
        if (ndvi != null) {
            opportunity.setCurrentNdvi(toBigDecimal(ndvi));
        }

        opportunityRepository.save(opportunity);
        log.info("Farm opportunity upserted (harvest ready): farmId={}", farmId);
    }

    // yield.predicted → enriches the opportunity with yield data
    @RabbitListener(queues = RabbitMQConfig.YIELD_PREDICTED_QUEUE)
    public void onYieldPredicted(Map<String, Object> payload) {
        String farmIdStr = (String) payload.get("farm_id");
        if (farmIdStr == null) return;

        log.info("yield.predicted received for farmId={}", farmIdStr);
        UUID farmId = UUID.fromString(farmIdStr);

        FarmOpportunity opportunity = opportunityRepository.findByFarmId(farmId)
                .orElseGet(() -> FarmOpportunity.builder()
                        .farmId(farmId)
                        .agriScore(50)
                        .existingBidsCount(0)
                        .createdAt(OffsetDateTime.now())
                        .build());

        opportunity.setPredictedYieldMinQuintals(
                toBigDecimal(payload.get("predicted_min_quintals")));
        opportunity.setPredictedYieldMaxQuintals(
                toBigDecimal(payload.get("predicted_max_quintals")));
        opportunity.setPredictedYieldMeanQuintals(
                toBigDecimal(payload.get("predicted_mean_quintals")));

        Object conf = payload.get("confidence_pct");
        if (conf != null) {
            opportunity.setYieldConfidencePct(toInt(conf));
        }

        String cropCycleId = (String) payload.get("crop_cycle_id");
        if (cropCycleId != null) opportunity.setCropCycleId(cropCycleId);

        opportunityRepository.save(opportunity);
        log.info("Farm opportunity enriched with yield data: farmId={}", farmId);
    }

    // ndvi.updated → keeps NDVI current in the marketplace listing
    @RabbitListener(queues = RabbitMQConfig.NDVI_UPDATED_QUEUE)
    public void onNdviUpdated(Map<String, Object> payload) {
        String farmIdStr = (String) payload.get("farm_id");
        if (farmIdStr == null) return;

        UUID farmId = UUID.fromString(farmIdStr);

        opportunityRepository.findByFarmId(farmId).ifPresent(opportunity -> {
            Object ndvi = payload.get("ndvi_value");
            if (ndvi != null) opportunity.setCurrentNdvi(toBigDecimal(ndvi));

            String healthStatus = (String) payload.get("health_status");
            if (healthStatus != null) opportunity.setNdviHealthStatus(healthStatus);

            opportunityRepository.save(opportunity);
            log.info("Farm opportunity NDVI updated: farmId={}", farmId);
        });
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return null;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(val.toString()); } catch (Exception e) { return null; }
    }

    private int toInt(Object val) {
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return 0; }
    }
}
