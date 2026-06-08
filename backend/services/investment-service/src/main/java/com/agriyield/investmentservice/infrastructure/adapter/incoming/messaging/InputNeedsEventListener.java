package com.agriyield.investmentservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.investmentservice.application.port.incoming.ListingServicePort;
import com.agriyield.investmentservice.application.port.outgoing.FarmServicePort;
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
    private final FarmServicePort farmServicePort;   // ADDED: to resolve farmer_id from farm context

    /**
     * IS-04: Consumes farm.exchange / input.needs.created event
     * and automatically creates a farm listing.
     *
     * ROOT CAUSE FIX: The original code fell back to farmId when
     * farmer_id was absent from the event:
     *
     *   UUID farmerId = event.containsKey("farmer_id")
     *       ? UUID.fromString((String) event.get("farmer_id"))
     *       : farmId;  // ← BUG: farmId != farmerId (farmer's user UUID)
     *
     * This caused every listing and every downstream voucher to be
     * saved with farmerId = farmId, making vouchers invisible to the
     * farmer when they call GET /api/v1/vouchers/my (which queries by
     * their JWT user ID, not their farm ID).
     *
     * FIX: Always resolve farmerId via gRPC from farm-service.
     * The event field "farmer_id" is used if present (fast path),
     * otherwise we call FarmServicePort.getFarmContext() which is the
     * authoritative source of truth.
     */
    @RabbitListener(queues = "investment.input-needs.queue")
    public void onInputNeedsCreated(Map<String, Object> event) {
        log.info("IS-04: Received input.needs.created event: {}", event);
        try {
            UUID farmId      = UUID.fromString((String) event.get("farm_id"));
            UUID inputNeedId = UUID.fromString((String) event.get("input_need_id"));
            UUID cropCycleId = UUID.fromString((String) event.get("crop_cycle_id"));

            // total_amount_etb may come as Double or String
            BigDecimal totalAmount =
                    new BigDecimal(event.get("total_amount_etb").toString());

            String cropType   = (String) event.getOrDefault("crop_type",   "UNKNOWN");
            String region     = (String) event.getOrDefault("region",      "UNKNOWN");
            String kebeleCode = (String) event.getOrDefault("kebele_code", "UNKNOWN");
            String seasonName = (String) event.getOrDefault("season_name", "UNKNOWN");
            int agriScore     = event.containsKey("agri_score")
                    ? Integer.parseInt(event.get("agri_score").toString()) : 50;

            // ── FIXED: Resolve farmerId correctly ────────────────────────────
            // Priority 1: event contains farmer_id AND it differs from farm_id
            //             (i.e. farm-service was already publishing it correctly)
            // Priority 2: resolve via gRPC — authoritative source of truth
            // NEVER fall back to farmId — that is a farm UUID, not a user UUID.
            UUID farmerId = resolveFarmerId(event, farmId);
            log.info("IS-04: Resolved farmerId={} for farmId={}", farmerId, farmId);

            listingService.createFromInputNeeds(
                    farmId, farmerId, inputNeedId, cropCycleId,
                    totalAmount, cropType, region, kebeleCode, seasonName, agriScore);

            log.info("IS-04: Listing created successfully for farm: {}", farmId);

        } catch (Exception e) {
            log.error("IS-04: Failed to process input.needs.created event: {}",
                    e.getMessage(), e);
            // Dead-letter queue will handle retries
        }
    }

    /**
     * Resolve the farmer's user UUID from the event or via gRPC.
     *
     * We treat the event field as valid only when it is present AND
     * differs from farm_id — the old bug was that farm-service
     * published farmer_id = farm_id, so we can't trust them being
     * equal.
     */
    private UUID resolveFarmerId(Map<String, Object> event, UUID farmId) {
        if (event.containsKey("farmer_id")) {
            UUID fromEvent = UUID.fromString((String) event.get("farmer_id"));
            if (!fromEvent.equals(farmId)) {
                // Event has a real farmer user UUID — use it directly.
                return fromEvent;
            }
            // farmer_id == farm_id means the publishing side had the same
            // bug — fall through to gRPC resolution below.
            log.warn("IS-04: event farmer_id == farm_id ({}) — resolving via gRPC", farmId);
        }

        // Authoritative lookup: ask farm-service for the owner of this farm.
        try {
            FarmServicePort.FarmContext ctx = farmServicePort.getFarmContext(farmId);
            UUID resolved = ctx.farmerId();
            if (resolved == null || resolved.equals(farmId)) {
                throw new IllegalStateException(
                        "FarmContext returned farmerId == farmId (" + farmId + "). " +
                                "farm-service gRPC response is missing farmer_id field.");
            }
            log.info("IS-04: Resolved farmerId={} via gRPC for farm={}", resolved, farmId);
            return resolved;
        } catch (Exception e) {
            // gRPC failed — this is a hard error, not a silent fallback.
            // Throw so the message goes to DLQ and gets retried/alerted.
            throw new RuntimeException(
                    "IS-04: Cannot resolve farmerId for farm " + farmId +
                            " — gRPC getFarmContext failed: " + e.getMessage(), e);
        }
    }
}