package com.agriyield.merchantservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.merchantservice.application.port.outgoing.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(String exchange, String routingKey, Map<String, Object> payload) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, payload);
            log.info("Event published: exchange={} routingKey={}", exchange, routingKey);
        } catch (Exception e) {
            log.error("Failed to publish event: exchange={} routingKey={} error={}", exchange, routingKey, e.getMessage());
        }
    }
}
