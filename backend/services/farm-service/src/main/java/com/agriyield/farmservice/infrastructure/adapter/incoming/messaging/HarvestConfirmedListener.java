package com.agriyield.farmservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.farmservice.application.port.incoming.FarmServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * SRS §3.3.4 + §5.2 — Consumes harvest.confirmed from offtaker.exchange.
 *
 * When the off-taker confirms receipt of the harvest at the factory,
 * this listener triggers the seasonal Agri-Score recalculation on farm-service.
 *
 * Expected payload (published by offtaker-service, SRS §5.2):
 * {
 *   "event_type"               : "harvest.confirmed",
 *   "farm_id"                  : "<UUID>",
 *   "agreement_id"             : "<UUID>",
 *   "actual_quantity_quintals"  : 21.5,
 *   "quality_grade"            : "A",
 *   "total_payment_etb"        : 45000.00,
 *   "confirmed_at"             : "2026-06-14T10:00:00Z",
 *
 *   // Optional fields farm-service uses for Agri-Score:
 *   "crop_cycle_id"            : "<UUID>",      // required for score save
 *   "contract_fulfilled"       : true,           // default true when harvest confirmed
 *   "repayment_completed"      : false,          // default false — set by escrow later
 *   "agronomist_rating"        : 4               // 1-5, default 0 if no visit
 * }
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HarvestConfirmedListener {

    private final FarmServicePort farmService;

    @RabbitListener(queues = "farm.harvest-events.queue")
    public void onHarvestConfirmed(Map<String, Object> event) {
        try {
            log.info("Received harvest event: {}", event);
            if (event == null || event.isEmpty()) return;

            String eventType = (String) event.getOrDefault("event_type", "");

            if (!"harvest.confirmed".equals(eventType)) {
                log.debug("Unhandled harvest event type: {} — ignoring", eventType);
                return;
            }

            // --- Required fields ---
            Object farmIdObj  = event.get("farm_id");
            Object cycleIdObj = event.get("crop_cycle_id");

            if (farmIdObj == null) {
                log.warn("harvest.confirmed missing farm_id — skipping: {}", event);
                return;
            }

            UUID farmId = UUID.fromString(farmIdObj.toString());

            if (cycleIdObj == null) {
                log.warn("harvest.confirmed missing crop_cycle_id for farm {} " +
                        "— Agri-Score calculation skipped", farmId);
                return;
            }

            UUID cropCycleId = UUID.fromString(cycleIdObj.toString());

            // --- Optional fields with safe defaults ---

            // harvest.confirmed means the off-taker received goods → contract fulfilled
            boolean contractFulfilled = getBooleanOrDefault(event, "contract_fulfilled", true);

            // repayment_completed is finalised by escrow-service after settlement,
            // so it defaults to false at this point
            boolean repaymentCompleted = getBooleanOrDefault(event, "repayment_completed", false);

            // agronomist_rating: 1-5, default 0 means no agronomist visited this season
            int agronomistRating = getIntOrDefault(event, "agronomist_rating", 0);

            log.info("Processing Agri-Score for farm={} cropCycle={} " +
                            "contractFulfilled={} repaymentCompleted={} agronomistRating={}",
                    farmId, cropCycleId, contractFulfilled, repaymentCompleted, agronomistRating);

            farmService.calculateAndSaveAgriScore(
                    farmId,
                    cropCycleId,
                    contractFulfilled,
                    repaymentCompleted,
                    agronomistRating);

            log.info("Agri-Score calculation completed successfully for farm: {}", farmId);

        } catch (IllegalArgumentException e) {
            log.error("harvest.confirmed contained invalid UUID: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to process harvest.confirmed event: {}", e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers — safe type coercion matching the pattern in InvestmentEventListener
    // -------------------------------------------------------------------------

    private boolean getBooleanOrDefault(Map<String, Object> event,
                                        String key,
                                        boolean defaultValue) {
        Object val = event.get(key);
        if (val == null) return defaultValue;
        if (val instanceof Boolean) return (Boolean) val;
        return Boolean.parseBoolean(val.toString());
    }

    private int getIntOrDefault(Map<String, Object> event,
                                String key,
                                int defaultValue) {
        Object val = event.get(key);
        if (val == null) return defaultValue;
        try {
            return ((Number) val).intValue();
        } catch (ClassCastException e) {
            return Integer.parseInt(val.toString());
        }
    }
}