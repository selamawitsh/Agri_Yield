package com.agriyield.geospatialservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FarmEventListener {

    private final GeospatialServicePort geospatialService;

    @RabbitListener(queues = "geospatial.farm-registered.queue")
    public void onFarmRegistered(Map<String, Object> event) {
        log.info("GS: received farm.registered event: {}", event);
        try {
            String farmId = (String) event.get("farm_id");
            if (farmId == null) {
                log.error("GS: farm.registered event missing farm_id — ignoring");
                return;
            }

            // ── Step 1: store boundary polygon using event data (no gRPC) ──
            String geoJson = (String) event.get("geo_json_polygon");
            Number lat     = (Number) event.get("gps_centroid_lat");
            Number lng     = (Number) event.get("gps_centroid_lng");
            Number areaHa  = (Number) event.get("area_hectares");

            if (geoJson != null && lat != null && lng != null) {
                geospatialService.registerFarmPolygon(
                        UUID.fromString(farmId),
                        geoJson,
                        lat.doubleValue(),
                        lng.doubleValue(),
                        areaHa != null ? areaHa.doubleValue() : null
                );
                log.info("GS: farm boundary stored for farmId={}", farmId);
            } else {
                log.warn("GS: farm.registered event missing polygon data for farmId={}" +
                        " — boundary NOT stored", farmId);
            }

            // ── Step 2: start monitoring (uses boundary from MongoDB, no gRPC) ──
            boolean started = geospatialService.startMonitoring(UUID.fromString(farmId));

            // Only log success if it actually succeeded
            if (started) {
                log.info("GS: monitoring started successfully for farm={}", farmId);
            } else {
                log.error("GS: monitoring FAILED to start for farm={}", farmId);
            }

        } catch (Exception e) {
            log.error("GS: failed processing farm.registered event: {}", e.getMessage(), e);
        }
    }
}