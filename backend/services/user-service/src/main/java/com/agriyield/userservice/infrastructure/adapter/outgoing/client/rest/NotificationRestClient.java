package com.agriyield.userservice.infrastructure.adapter.outgoing.client.rest;

import com.agriyield.userservice.application.port.outgoing.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationRestClient implements NotificationPort {

    @Override
    public void sendSms(String phone, String message) {
        // Stub — real implementation via AfricasTalking API
        // when notification-service is fully built
        log.info("SMS STUB → {}: {}", phone, message);
    }
}
