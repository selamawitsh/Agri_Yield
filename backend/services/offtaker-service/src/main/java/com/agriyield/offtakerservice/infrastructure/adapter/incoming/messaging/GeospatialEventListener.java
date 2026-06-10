package com.agriyield.offtakerservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.offtakerservice.infrastructure.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class GeospatialEventListener {

    @RabbitListener(queues = RabbitMQConfig.HARVEST_PREDICTED_QUEUE)
    public void onHarvestPredicted(Map<String, Object> payload) {
        String farmId = (String) payload.get("farm_id");
        String estimatedFrom = (String) payload.get("estimated_harvest_date_from");
        String estimatedTo   = (String) payload.get("estimated_harvest_date_to");
        log.info("harvest.predicted received: farmId={} window={} to {}",
                farmId, estimatedFrom, estimatedTo);
        // Farm is now visible to off-takers in marketplace.
        // Search index refresh would happen here in a full implementation.
    }

    @RabbitListener(queues = RabbitMQConfig.YIELD_PREDICTED_QUEUE)
    public void onYieldPredicted(Map<String, Object> payload) {
        String farmId = (String) payload.get("farm_id");
        Object yieldMean = payload.get("predicted_mean_quintals");
        log.info("yield.predicted received: farmId={} predictedMean={}", farmId, yieldMean);
    }
}
