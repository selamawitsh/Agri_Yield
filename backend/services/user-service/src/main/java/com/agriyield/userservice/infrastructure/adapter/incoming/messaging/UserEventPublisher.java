package com.agriyield.userservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserPreRegistered(User user) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "user.pre_registered");
        event.put("user_id", user.getId().toString());
        event.put("phone", user.getPhone());
        event.put("fayda_id", user.getFaydaId());
        event.put("role", user.getRole().getValue());
        event.put("kyc_status", user.getKycStatus().getValue());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing user.pre_registered event for user: {}", user.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            "user.pre_registered",
            event
        );
    }

    // SRS Section 3.2.4 — full_name is required in this payload
    public void publishUserRegistered(User user, String fullName) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "user.registered");
        event.put("user_id", user.getId().toString());
        event.put("phone", user.getPhone());
        event.put("email", user.getEmail() != null ? user.getEmail() : "");
        event.put("fayda_id", user.getFaydaId());
        event.put("role", user.getRole().getValue());
        event.put("full_name", fullName != null ? fullName : "");
        event.put("preferred_language", user.getPreferredLanguage().getCode());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing user.registered event for user: {}", user.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            RabbitMQConfig.USER_REGISTERED_KEY,
            event
        );
    }

    public void publishUserKycVerified(User user) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "user.kyc.verified");
        event.put("user_id", user.getId().toString());
        event.put("fayda_id", user.getFaydaId());
        event.put("kyc_status", "VERIFIED");
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing user.kyc.verified event for user: {}", user.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            RabbitMQConfig.USER_KYC_VERIFIED_KEY,
            event
        );
    }

    public void publishUserSuspended(User user, UUID adminId, String reason) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "user.suspended");
        event.put("user_id", user.getId().toString());
        event.put("reason", reason);
        event.put("suspended_by_admin_id", adminId.toString());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing user.suspended event for user: {}", user.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            RabbitMQConfig.USER_SUSPENDED_KEY,
            event
        );
    }

    public void publishUserReactivated(User user, UUID adminId) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "user.reactivated");
        event.put("user_id", user.getId().toString());
        event.put("reactivated_by_admin_id", adminId.toString());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing user.reactivated event for user: {}", user.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_EXCHANGE,
            RabbitMQConfig.USER_REACTIVATED_KEY,
            event
        );
    }
}
