package com.agriyield.userservice.infrastructure.adapter.outgoing.client.rest;

import com.agriyield.userservice.core.port.outgoing.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRestClient implements NotificationPort {
    
    @Value("${app.notification-service.url:http://localhost:8093}")
    private String notificationServiceUrl;
    
    @Override
    public void sendSms(String phone, String message) {
        // For now, just log the SMS (since notification service might not be running)
        log.info("SMS would be sent to: {} - Message: {}", phone, message);
        // In production, this would make an HTTP call to notification-service
    }
    
    @Override
    public void sendEmail(String email, String subject, String body) {
        log.info("Email would be sent to: {} - Subject: {}", email, subject);
    }
    
    @Override
    public void sendPushNotification(String userId, String title, String body, Map<String, String> data) {
        log.info("Push notification would be sent to user: {} - Title: {}", userId, title);
    }
}
