package com.agriyield.voucherservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentEventListener {

    private final VoucherServicePort voucherService;

    /** Triggered when a listing becomes fully funded — generate vouchers automatically */
    @RabbitListener(queues = "voucher.listing-funded.queue")
    public void onListingFullyFunded(Map<String, Object> event) {
        log.info("Received listing.fully.funded event: {}", event);
        try {
            String listingId   = (String) event.get("listing_id");
            String farmId      = (String) event.get("farm_id");
            String farmerId    = (String) event.get("farmer_id");
            String inputNeedId = (String) event.get("input_need_id");
            String cropCycleId = (String) event.get("crop_cycle_id");

            if (listingId == null || farmId == null) {
                log.error("listing.fully.funded event missing required fields: {}", event);
                return;
            }

            voucherService.generateForInvestment(
                UUID.fromString(listingId),   // listingId == investmentId in our model
                UUID.fromString(farmId),
                UUID.fromString(farmerId),
                UUID.fromString(inputNeedId),
                UUID.fromString(cropCycleId));

            log.info("Vouchers generated for fully funded listing: {}", listingId);
        } catch (Exception e) {
            log.error("Failed to generate vouchers from listing.fully.funded event: {}",
                e.getMessage(), e);
        }
    }

    /** Triggered when an investment is cancelled — cancel all its vouchers */
    @RabbitListener(queues = "voucher.investment-cancelled.queue")
    public void onInvestmentCancelled(Map<String, Object> event) {
        log.info("Received investment.cancelled event: {}", event);
        try {
            String investmentId = (String) event.get("investment_id");
            if (investmentId == null) {
                log.error("investment.cancelled event missing investment_id");
                return;
            }
            voucherService.cancelForInvestment(UUID.fromString(investmentId));
            log.info("Vouchers cancelled for investment: {}", investmentId);
        } catch (Exception e) {
            log.error("Failed to cancel vouchers from investment.cancelled event: {}",
                e.getMessage(), e);
        }
    }
}
