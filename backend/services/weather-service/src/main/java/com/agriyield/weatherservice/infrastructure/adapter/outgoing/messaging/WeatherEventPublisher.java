package com.agriyield.weatherservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.weatherservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.domain.model.WeatherAlert;
import com.agriyield.weatherservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishWeatherAlert(UUID farmId, WeatherAlert alert) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "weather.alert");
        event.put("farm_id", farmId.toString());
        event.put("alert_type", alert.getAlertType().name());
        event.put("severity", alert.getSeverity().name());
        event.put("forecast_value", alert.getForecastValue());
        event.put("forecast_date", alert.getForecastDate() != null ? alert.getForecastDate().toString() : null);
        event.put("message_en", alert.getMessageEn());
        event.put("message_am", alert.getMessageAm());
        event.put("message_om", alert.getMessageOm());
        event.put("timestamp", OffsetDateTime.now().toString());

        log.info("Publishing weather.alert for farm: {} type: {}", farmId, alert.getAlertType());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.WEATHER_EXCHANGE,
                RabbitMQConfig.WEATHER_ALERT_KEY,
                event);
    }

    // WS-04
    @Override
    public void publishDroughtTriggered(UUID farmId, DroughtCondition condition) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "drought.triggered");
        event.put("farm_id", farmId.toString());
        event.put("consecutive_dry_days", condition.getConsecutiveDryDays());
        event.put("trigger_date", OffsetDateTime.now().toString());
        event.put("refund_pct", 20); // SRS: 20% parametric insurance refund
        event.put("timestamp", OffsetDateTime.now().toString());

        log.warn("Publishing drought.triggered for farm: {}", farmId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.WEATHER_EXCHANGE,
                RabbitMQConfig.DROUGHT_TRIGGERED_KEY,
                event);
    }
}
