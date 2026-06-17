package com.agriyield.offtakerservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.offtakerservice.application.port.outgoing.FarmOpportunityRepositoryPort;
import com.agriyield.offtakerservice.application.port.outgoing.FarmServicePort;
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
    private final FarmServicePort farmServicePort;

    @RabbitListener(queues = RabbitMQConfig.HARVEST_PREDICTED_QUEUE)
    public void onHarvestPredicted(Map<String, Object> payload) {
        String farmIdStr = (String) payload.get("farm_id");
        if (farmIdStr == null) return;

        log.info("harvest.predicted received for farmId={}", farmIdStr);
        UUID farmId = UUID.fromString(farmIdStr);

        FarmOpportunity opportunity = findOrCreateOpportunity(farmId);
        backfillStaticFieldsIfMissing(opportunity);

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

    @RabbitListener(queues = RabbitMQConfig.YIELD_PREDICTED_QUEUE)
    public void onYieldPredicted(Map<String, Object> payload) {
        String farmIdStr = (String) payload.get("farm_id");
        if (farmIdStr == null) return;

        log.info("yield.predicted received for farmId={}", farmIdStr);
        UUID farmId = UUID.fromString(farmIdStr);

        FarmOpportunity opportunity = findOrCreateOpportunity(farmId);
        backfillStaticFieldsIfMissing(opportunity);

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

    @RabbitListener(queues = RabbitMQConfig.NDVI_UPDATED_QUEUE)
    public void onNdviUpdated(Map<String, Object> payload) {
        String farmIdStr = (String) payload.get("farm_id");
        if (farmIdStr == null) return;

        UUID farmId = UUID.fromString(farmIdStr);

        opportunityRepository.findByFarmId(farmId).ifPresent(opportunity -> {
            backfillStaticFieldsIfMissing(opportunity);

            Object ndvi = payload.get("ndvi_value");
            if (ndvi != null) opportunity.setCurrentNdvi(toBigDecimal(ndvi));

            String healthStatus = (String) payload.get("health_status");
            if (healthStatus != null) opportunity.setNdviHealthStatus(healthStatus);

            opportunityRepository.save(opportunity);
            log.info("Farm opportunity NDVI updated: farmId={}", farmId);
        });
    }

    private FarmOpportunity findOrCreateOpportunity(UUID farmId) {
        return opportunityRepository.findByFarmId(farmId)
                .orElseGet(() -> FarmOpportunity.builder()
                        .farmId(farmId)
                        .agriScore(50)
                        .existingBidsCount(0)
                        .createdAt(OffsetDateTime.now())
                        .build());
    }

    private void backfillStaticFieldsIfMissing(FarmOpportunity opportunity) {
        boolean missingStaticData = isBlank(opportunity.getCropType())
                || isBlank(opportunity.getRegion())
                || isBlank(opportunity.getFarmerId());

        if (!missingStaticData) {
            return;
        }

        try {
            Map<String, Object> farmData = farmServicePort.getFarmById(opportunity.getFarmId().toString());

            if (isBlank(opportunity.getCropType())) {
                opportunity.setCropType((String) farmData.get("cropType"));
            }
            if (isBlank(opportunity.getRegion())) {
                opportunity.setRegion((String) farmData.get("region"));
            }
            if (isBlank(opportunity.getFarmerId())) {
                opportunity.setFarmerId((String) farmData.get("farmerId"));
            }
            if (isBlank(opportunity.getKebeleCode())) {
                opportunity.setKebeleCode((String) farmData.get("kebeleCode"));
            }
            if (opportunity.getGpsCentroidLat() == null
                    || opportunity.getGpsCentroidLat().compareTo(BigDecimal.ZERO) == 0) {
                opportunity.setGpsCentroidLat(toBigDecimal(farmData.get("gpsCentroidLat")));
            }
            if (opportunity.getGpsCentroidLng() == null
                    || opportunity.getGpsCentroidLng().compareTo(BigDecimal.ZERO) == 0) {
                opportunity.setGpsCentroidLng(toBigDecimal(farmData.get("gpsCentroidLng")));
            }
            if (opportunity.getAreaHectares() == null
                    || opportunity.getAreaHectares().compareTo(BigDecimal.ZERO) == 0) {
                opportunity.setAreaHectares(toBigDecimal(farmData.get("areaHectares")));
            }
            if (isBlank(opportunity.getCropCycleStatus())) {
                opportunity.setCropCycleStatus("GROWING");
            }

            log.info("Backfilled static farm data for farmId={}: cropType={} region={}",
                    opportunity.getFarmId(), opportunity.getCropType(), opportunity.getRegion());

        } catch (Exception e) {
            log.warn("Could not backfill farm static data for farmId={}: {}",
                    opportunity.getFarmId(), e.getMessage());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
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