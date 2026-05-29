package com.agriyield.voucherservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentEventListener {

    private final VoucherServicePort voucherService;

    /**
     * SRS §3.5: Consume listing.fully.funded → generate vouchers.
     *
     * investment-service publishListingFullyFunded() payload:
     * {
     *   farm_id, farmer_id, input_need_id, crop_cycle_id, listing_id,
     *   investments: [{ investment_id, investor_id, amount_etb, input_need_id }]
     * }
     * All fields we need are in the payload — NO gRPC call needed.
     */
    @RabbitListener(queues = "voucher.listing-funded.queue")
    public void onListingFullyFunded(Map<String, Object> event) {
        log.info("VS: received listing.fully.funded event: {}", event);
        try {
            // ── Extract top-level fields from payload ─────────────────────────
            String farmId      = (String) event.get("farm_id");
            String farmerId    = (String) event.get("farmer_id");
            String inputNeedId = (String) event.get("input_need_id");
            String cropCycleId = (String) event.get("crop_cycle_id");
            String listingId   = (String) event.get("listing_id");

            if (farmId == null || listingId == null) {
                log.error("VS: listing.fully.funded missing farm_id or listing_id — ignoring: {}",
                        event);
                return;
            }

            if (farmerId == null || inputNeedId == null || cropCycleId == null) {
                log.error("VS: listing.fully.funded missing farmer_id/input_need_id/crop_cycle_id" +
                        " — cannot generate vouchers. Event: {}", event);
                return;
            }

            UUID farmUUID      = UUID.fromString(farmId);
            UUID farmerUUID    = UUID.fromString(farmerId);
            UUID inputNeedUUID = UUID.fromString(inputNeedId);
            UUID cropCycleUUID = UUID.fromString(cropCycleId);

            // ── Try per-investment generation from investments array ───────────
            Object investmentsObj = event.get("investments");
            boolean generated = false;

            if (investmentsObj instanceof List<?> investments && !investments.isEmpty()) {
                for (Object o : investments) {
                    if (!(o instanceof Map<?, ?> inv)) continue;

                    String investmentId = (String) inv.get("investment_id");
                    if (investmentId == null) {
                        log.warn("VS: investments entry missing investment_id — skipping: {}", inv);
                        continue;
                    }

                    // Per-investment input_need_id takes priority if present
                    String itemInputNeedId = (String) inv.get("input_need_id");
                    UUID resolvedInputNeedId = itemInputNeedId != null
                            ? UUID.fromString(itemInputNeedId)
                            : inputNeedUUID;

                    log.info("VS: generating vouchers for investmentId={} farmId={}",
                            investmentId, farmId);

                    voucherService.generateForInvestment(
                            UUID.fromString(investmentId),
                            farmUUID,
                            farmerUUID,
                            resolvedInputNeedId,
                            cropCycleUUID
                    );
                    generated = true;
                }
            }

            if (!generated) {
                // Fallback: no investments array — use listing_id as investment reference
                log.warn("VS: no investments array — using listingId={} as fallback", listingId);
                voucherService.generateForInvestment(
                        UUID.fromString(listingId),
                        farmUUID,
                        farmerUUID,
                        inputNeedUUID,
                        cropCycleUUID
                );
            }

            log.info("VS: voucher generation complete for farm={}", farmId);

        } catch (Exception e) {
            log.error("VS: failed processing listing.fully.funded: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "voucher.investment-cancelled.queue")
    public void onInvestmentCancelled(Map<String, Object> event) {
        log.info("VS: received investment.cancelled event: {}", event);
        try {
            String investmentId = (String) event.get("investment_id");
            if (investmentId == null) {
                log.error("VS: investment.cancelled missing investment_id");
                return;
            }
            voucherService.cancelForInvestment(UUID.fromString(investmentId));
            log.info("VS: vouchers cancelled for investment={}", investmentId);
        } catch (Exception e) {
            log.error("VS: failed processing investment.cancelled: {}", e.getMessage(), e);
        }
    }
}