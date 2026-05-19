package com.agriyield.userservice.core.port.outgoing;

import java.util.Map;

public interface NotificationPort {
    void sendSms(String phone, String message);
    void sendEmail(String email, String subject, String body);
    void sendPushNotification(String userId, String title, String body, Map<String, String> data);
}