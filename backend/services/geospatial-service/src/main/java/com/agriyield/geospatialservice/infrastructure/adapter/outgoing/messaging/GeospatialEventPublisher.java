package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.geospatialservice.application.port.outgoing.GeospatialEventPublisherPort;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;
import com.agriyield.geospatialservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeospatialEventPublisher implements GeospatialEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishNdviUpdated(NdviReading reading, double changeFromPrevious) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "ndvi.updated");
        event.put("farm_id", reading.getFarmId().toString());
        event.put("ndvi_value", reading.getNdviValue());
        event.put("ndvi_change_from_previous", changeFromPrevious);
        event.put("cloud_coverage_pct", reading.getCloudCoverage());
        event.put("health_status", reading.getHealthStatus());
        event.put("recorded_date", reading.getRecordedDate().toString());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("GS: publishing ndvi.updated farm={} ndvi={}",
            reading.getFarmId(), reading.getNdviValue());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.GEOSPATIAL_EXCHANGE,
            RabbitMQConfig.NDVI_UPDATED_KEY, event);
    }

    @Override
    public void publishYieldPredicted(YieldPrediction prediction) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "yield.predicted");
        event.put("farm_id", prediction.getFarmId().toString());
        if (prediction.getCropCycleId() != null)
            event.put("crop_cycle_id", prediction.getCropCycleId().toString());
        event.put("predicted_min_quintals", prediction.getTotalYieldMinQuintals());
        event.put("predicted_max_quintals", prediction.getTotalYieldMaxQuintals());
        event.put("predicted_mean_quintals", prediction.getTotalYieldMeanQuintals());
        event.put("confidence_pct", prediction.getConfidencePct());
        event.put("weeks_to_harvest", prediction.getWeeksToHarvest());
        event.put("model_version", prediction.getModelVersion());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("GS: publishing yield.predicted farm={} mean={}",
            prediction.getFarmId(), prediction.getTotalYieldMeanQuintals());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.GEOSPATIAL_EXCHANGE,
            RabbitMQConfig.YIELD_PREDICTED_KEY, event);
    }

    @Override
    public void publishHarvestPredicted(UUID farmId,
                                         String estimatedDateFrom,
                                         String estimatedDateTo,
                                         double currentNdvi) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "harvest.predicted");
        event.put("farm_id", farmId.toString());
        event.put("estimated_harvest_date_from", estimatedDateFrom);
        event.put("estimated_harvest_date_to", estimatedDateTo);
        event.put("readiness_signal", "NDVI_DECLINING");
        event.put("current_ndvi", currentNdvi);
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("GS: publishing harvest.predicted farm={}", farmId);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.GEOSPATIAL_EXCHANGE,
            RabbitMQConfig.HARVEST_PREDICTED_KEY, event);
    }
}
