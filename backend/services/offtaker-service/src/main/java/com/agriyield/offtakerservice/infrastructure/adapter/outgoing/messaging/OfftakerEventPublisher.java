package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.offtakerservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.offtakerservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OfftakerEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishBidPlaced(UUID bidId, UUID farmId, UUID offtakerId,
                                  BigDecimal quantityQuintals, BigDecimal pricePerQuintalEtb,
                                  BigDecimal totalValueEtb, String expiresAt) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event_type", "bid.placed");
        payload.put("bid_id", bidId.toString());
        payload.put("farm_id", farmId.toString());
        payload.put("offtaker_id", offtakerId.toString());
        payload.put("quantity_quintals", quantityQuintals);
        payload.put("price_per_quintal_etb", pricePerQuintalEtb);
        payload.put("total_value_etb", totalValueEtb);
        payload.put("expires_at", expiresAt);
        payload.put("timestamp", OffsetDateTime.now().toString());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.OFFTAKER_EXCHANGE,
                RabbitMQConfig.BID_PLACED_KEY,
                payload);
        log.info("Published bid.placed for bidId={}", bidId);
    }

    @Override
    public void publishBidAccepted(UUID bidId, UUID farmId, UUID farmerId,
                                    UUID offtakerId, UUID agreementId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event_type", "bid.accepted");
        payload.put("bid_id", bidId.toString());
        payload.put("farm_id", farmId.toString());
        payload.put("farmer_id", farmerId.toString());
        payload.put("offtaker_id", offtakerId.toString());
        payload.put("agreement_id", agreementId.toString());
        payload.put("timestamp", OffsetDateTime.now().toString());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.OFFTAKER_EXCHANGE,
                RabbitMQConfig.BID_ACCEPTED_KEY,
                payload);
        log.info("Published bid.accepted for bidId={}", bidId);
    }

    @Override
    public void publishHarvestConfirmed(UUID farmId, UUID agreementId,
                                         BigDecimal actualQuantityQuintals,
                                         String qualityGrade, BigDecimal totalPaymentEtb) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event_type", "harvest.confirmed");
        payload.put("farm_id", farmId.toString());
        payload.put("agreement_id", agreementId.toString());
        payload.put("actual_quantity_quintals", actualQuantityQuintals);
        payload.put("quality_grade", qualityGrade);
        payload.put("total_payment_etb", totalPaymentEtb);
        payload.put("confirmed_at", OffsetDateTime.now().toString());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.OFFTAKER_EXCHANGE,
                RabbitMQConfig.HARVEST_CONFIRMED_KEY,
                payload);
        log.info("Published harvest.confirmed for farmId={}", farmId);
    }

    @Override
    public void publishOfftakerDefaulted(UUID bidId, UUID farmId,
                                          UUID offtakerId, BigDecimal forfeitAmountEtb) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("event_type", "offtaker.defaulted");
        payload.put("bid_id", bidId.toString());
        payload.put("farm_id", farmId.toString());
        payload.put("offtaker_id", offtakerId.toString());
        payload.put("forfeit_amount_etb", forfeitAmountEtb);
        payload.put("timestamp", OffsetDateTime.now().toString());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.OFFTAKER_EXCHANGE,
                RabbitMQConfig.OFFTAKER_DEFAULTED_KEY,
                payload);
        log.info("Published offtaker.defaulted for bidId={}", bidId);
    }
}
