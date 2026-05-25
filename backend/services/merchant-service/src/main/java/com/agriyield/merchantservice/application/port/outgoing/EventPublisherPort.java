package com.agriyield.merchantservice.application.port.outgoing;

import java.util.Map;

public interface EventPublisherPort {
    void publish(String exchange, String routingKey, Map<String, Object> payload);
}
