package com.agriyield.userservice.application.port.outgoing;

public interface NotificationPort {
    void sendSms(String phone, String message);
}
