package com.agriyield.fraudservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.fraudservice.application.port.outgoing.FraudEventPublisherPort;
import com.agriyield.fraudservice.domain.model.FraudAlert;
import com.agriyield.fraudservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudEventPublisher implements FraudEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishFraudAlertHigh(FraudAlert alert) {
        Map<String, Object> event = buildEvent(alert);
        log.warn("Publishing fraud.alert.high: type={} entity={}",
            alert.getAlertType(), alert.getEntityId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FRAUD_EXCHANGE,
            RabbitMQConfig.FRAUD_ALERT_HIGH_KEY, event);
    }

    @Override
    public void publishFraudAlertCritical(FraudAlert alert) {
        Map<String, Object> event = buildEvent(alert);
        log.error("Publishing fraud.alert.critical: type={} entity={}",
            alert.getAlertType(), alert.getEntityId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.FRAUD_EXCHANGE,
            RabbitMQConfig.FRAUD_ALERT_CRITICAL_KEY, event);
    }

    private Map<String, Object> buildEvent(FraudAlert alert) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "fraud.alert." + alert.getSeverity().getValue().toLowerCase());
        event.put("alert_id", alert.getId() != null ? alert.getId().toString() : "");
        event.put("alert_type", alert.getAlertType().getValue());
        event.put("entity_type", alert.getEntityType().getValue());
        event.put("entity_id", alert.getEntityId() != null ? alert.getEntityId().toString() : "");
        event.put("severity", alert.getSeverity().getValue());
        event.put("description", alert.getDescription());
        event.put("evidence", alert.getEvidence());
        event.put("timestamp", LocalDateTime.now().toString());
        return event;
    }
}
