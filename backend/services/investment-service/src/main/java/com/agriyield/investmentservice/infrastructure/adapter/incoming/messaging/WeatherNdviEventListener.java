package com.agriyield.investmentservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.investmentservice.application.port.incoming.ListingServicePort;
import com.agriyield.investmentservice.domain.model.FarmListing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherNdviEventListener {

    private final ListingServicePort listingService;

    // ── NDVI Updated ──────────────────────────────────────────────────────────
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "investment.ndvi-updated.queue", durable = "true"),
        exchange = @Exchange(value = "geospatial.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key = "ndvi.updated"
    ))
    public void onNdviUpdated(Map<String, Object> event) {
        log.info("IS-10: received ndvi.updated event: {}", event);
        try {
            String farmId = (String) event.get("farm_id");
            if (farmId == null) return;

            double ndviValue  = parseDouble(event.get("ndvi_value"), 0.0);
            double expectedNdvi = 0.5;
            double diff = ndviValue - expectedNdvi;

            BigDecimal ndviBonus   = BigDecimal.ZERO;
            BigDecimal ndviPenalty = BigDecimal.ZERO;

            if (diff > 0) {
                ndviBonus = BigDecimal.valueOf(Math.min((diff / 0.05) * 0.5, 3.0));
            } else if (diff < 0) {
                ndviPenalty = BigDecimal.valueOf(Math.min((Math.abs(diff) / 0.05) * 0.5, 3.0));
            }

            updateListingsForFarm(UUID.fromString(farmId),
                ndviBonus, BigDecimal.ZERO, ndviPenalty, BigDecimal.ZERO,
                "NDVI update ndvi=" + ndviValue);

        } catch (Exception e) {
            log.error("IS-10: failed processing ndvi.updated: {}", e.getMessage(), e);
        }
    }

    // ── Weather Alert ─────────────────────────────────────────────────────────
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "investment.weather-alert.queue", durable = "true"),
        exchange = @Exchange(value = "weather.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key = "weather.alert"
    ))
    public void onWeatherAlert(Map<String, Object> event) {
        log.info("IS-10: received weather.alert event: {}", event);
        try {
            String farmId    = (String) event.get("farm_id");
            String alertType = (String) event.get("alert_type");
            if (farmId == null) return;

            BigDecimal weatherBonus   = BigDecimal.ZERO;
            BigDecimal weatherPenalty = BigDecimal.ZERO;

            if ("HEAVY_RAIN".equals(alertType)) {
                weatherBonus = BigDecimal.valueOf(0.5);
            } else if ("FROST_WARNING".equals(alertType) || "HEATWAVE".equals(alertType)) {
                weatherPenalty = BigDecimal.valueOf(1.0);
            }

            updateListingsForFarm(UUID.fromString(farmId),
                BigDecimal.ZERO, weatherBonus, BigDecimal.ZERO, weatherPenalty,
                "Weather alert: " + alertType);

        } catch (Exception e) {
            log.error("IS-10: failed processing weather.alert: {}", e.getMessage(), e);
        }
    }

    // ── Drought Triggered ─────────────────────────────────────────────────────
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "investment.drought-triggered.queue", durable = "true"),
        exchange = @Exchange(value = "weather.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key = "drought.triggered"
    ))
    public void onDroughtTriggered(Map<String, Object> event) {
        log.warn("IS-10: received drought.triggered event: {}", event);
        try {
            String farmId = (String) event.get("farm_id");
            if (farmId == null) return;

            int consecutiveDryDays = parseInt(event.get("consecutive_dry_days"), 30);
            int daysAbove15 = Math.max(0, consecutiveDryDays - 15);
            double droughtRisk = Math.min((daysAbove15 / 5.0) * 1.0, 4.0);

            log.warn("IS-10: drought risk -{} % APR for farm={} dryDays={}",
                droughtRisk, farmId, consecutiveDryDays);

            updateListingsForFarm(UUID.fromString(farmId),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.valueOf(droughtRisk),
                "Drought: " + consecutiveDryDays + " dry days");

            try {
                listingService.cancelListingsForFarm(UUID.fromString(farmId),
                    "Drought parametric insurance triggered after "
                    + consecutiveDryDays + " dry days");
            } catch (Exception e) {
                log.error("IS-10: could not cancel listings for farm={}: {}",
                    farmId, e.getMessage());
            }

        } catch (Exception e) {
            log.error("IS-10: failed processing drought.triggered: {}", e.getMessage(), e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private void updateListingsForFarm(UUID farmId,
                                       BigDecimal ndviBonus,
                                       BigDecimal weatherBonus,
                                       BigDecimal ndviPenalty,
                                       BigDecimal droughtRisk,
                                       String reason) {
        try {
            List<FarmListing> listings = listingService.getActiveListingsByFarmId(farmId);
            if (listings.isEmpty()) {
                log.debug("IS-10: no active listings for farm={}", farmId);
                return;
            }
            for (FarmListing listing : listings) {
                try {
                    listingService.updateApr(listing.getId(),
                        ndviBonus, weatherBonus, ndviPenalty, droughtRisk);
                    log.info("IS-10: APR updated for listing={} reason={}",
                        listing.getId(), reason);
                } catch (Exception e) {
                    log.error("IS-10: APR update failed for listing={}: {}",
                        listing.getId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("IS-10: could not fetch listings for farm={}: {}", farmId, e.getMessage());
        }
    }

    private double parseDouble(Object val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val.toString()); } catch (Exception e) { return def; }
    }

    private int parseInt(Object val, int def) {
        if (val == null) return def;
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return def; }
    }
}
