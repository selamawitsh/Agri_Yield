package com.agriyield.fraudservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.fraudservice.application.port.incoming.FraudServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherEventListener {

    private final FraudServicePort fraudService;

    /** Listen for voucher.redeemed — run merchant eligibility check post-redemption */
    @RabbitListener(queues = "fraud.voucher-redeemed.queue")
    public void onVoucherRedeemed(Map<String, Object> event) {
        log.info("FR: received voucher.redeemed event: {}", event);
        try {
            String merchantId = (String) event.get("merchant_id");
            String voucherCode = (String) event.get("voucher_code");
            if (merchantId == null || voucherCode == null) return;

            // Recalculate fraud score for merchant after each redemption
            fraudService.calculateFraudRiskScore(
                UUID.fromString(merchantId), "MERCHANT");

        } catch (Exception e) {
            log.error("FR: failed processing voucher.redeemed event: {}", e.getMessage());
        }
    }

    /** Listen for voucher.rejected — log rejection for fraud scoring */
    @RabbitListener(queues = "fraud.voucher-rejected.queue")
    public void onVoucherRejected(Map<String, Object> event) {
        log.info("FR: received voucher.rejected event: {}", event);
        try {
            String merchantId = (String) event.get("merchant_id");
            String reason = (String) event.get("rejection_reason");
            if (merchantId == null) return;

            log.warn("FR: voucher rejected for merchant={} reason={}", merchantId, reason);

            // Duplicate scan triggers are already handled via gRPC in real-time.
            // This listener handles async post-rejection score updates.
            fraudService.calculateFraudRiskScore(
                UUID.fromString(merchantId), "MERCHANT");

        } catch (Exception e) {
            log.error("FR: failed processing voucher.rejected event: {}", e.getMessage());
        }
    }
}
