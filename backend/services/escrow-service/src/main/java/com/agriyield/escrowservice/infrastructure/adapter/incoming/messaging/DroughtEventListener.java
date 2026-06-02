package com.agriyield.escrowservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.escrowservice.application.port.incoming.EscrowServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DroughtEventListener {

    private final EscrowServicePort escrowService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "escrow.drought-triggered.queue", durable = "true"),
        exchange = @Exchange(value = "weather.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key = "drought.triggered"
    ))
    public void onDroughtTriggered(Map<String, Object> event) {
        log.warn("ES: received drought.triggered event: {}", event);
        try {
            String farmId = (String) event.get("farm_id");
            if (farmId == null) {
                log.error("ES: drought.triggered missing farm_id — ignoring");
                return;
            }

            int consecutiveDryDays = event.containsKey("consecutive_dry_days")
                ? Integer.parseInt(event.get("consecutive_dry_days").toString()) : 30;
            int refundPct = event.containsKey("refund_pct")
                ? Integer.parseInt(event.get("refund_pct").toString()) : 20;

            log.warn("ES: drought parametric refund {}% for farm={} dryDays={}",
                refundPct, farmId, consecutiveDryDays);

            // Investment-service handles the investment cancellation which
            // triggers escrow cancel via escrowServicePort.cancel(investmentId)
            // So escrow refund flows automatically when investment is cancelled.
            log.info("ES: drought refund for farm={} will be processed via" +
                " investment cancellation chain", farmId);

        } catch (Exception e) {
            log.error("ES: failed processing drought.triggered: {}", e.getMessage(), e);
        }
    }
}
