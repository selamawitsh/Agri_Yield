package com.agriyield.farmservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.farmservice.application.port.incoming.FarmServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * SRS §3.3: Consumes farm.satellite.verified event from geospatial-service.
 * Updates farm status to VERIFIED and sets satellite_verified = true.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SatelliteVerifiedListener {

    private final FarmServicePort farmService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "farm.satellite-verified.queue", durable = "true"),
        exchange = @Exchange(value = "farm.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key = "farm.satellite.verified"
    ))
    public void onSatelliteVerified(Map<String, Object> event) {
        log.info("FS: received farm.satellite.verified event: {}", event);
        try {
            String farmId = (String) event.get("farm_id");
            if (farmId == null) {
                log.error("FS: farm.satellite.verified missing farm_id — ignoring");
                return;
            }

            String verificationStatus = (String) event.getOrDefault(
                "verification_status", "VERIFIED");
            double verifiedAreaHa = event.containsKey("verified_area_ha")
                ? Double.parseDouble(event.get("verified_area_ha").toString()) : 0.0;
            double ndviBaseline = event.containsKey("ndvi_baseline")
                ? Double.parseDouble(event.get("ndvi_baseline").toString()) : 0.0;

            log.info("FS: satellite verification farm={} status={} area={}ha ndvi={}",
                farmId, verificationStatus, verifiedAreaHa, ndviBaseline);

            // Retry up to 5 times with 1 second delay
            // Handles timing race where event arrives before farm DB transaction commits
            int maxRetries = 5;
            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    farmService.markSatelliteVerified(
                        UUID.fromString(farmId),
                        verificationStatus.equals("VERIFIED"),
                        verifiedAreaHa
                    );
                    log.info("FS: farm {} marked as {}", farmId, verificationStatus);
                    return;
                } catch (com.agriyield.farmservice.domain.exception.FarmNotFoundException e) {
                    if (attempt < maxRetries) {
                        log.warn("FS: farm {} not found yet (attempt {}/{}) — retrying in 1s",
                            farmId, attempt, maxRetries);
                        try { Thread.sleep(1000); } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    } else {
                        log.error("FS: farm {} not found after {} attempts — giving up",
                            farmId, maxRetries);
                    }
                }
            }

        } catch (Exception e) {
            log.error("FS: failed processing farm.satellite.verified: {}", e.getMessage(), e);
        }
    }
}
