package com.agriyield.weatherservice.application.port.outgoing;

import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.domain.model.WeatherAlert;

import java.util.UUID;

public interface EventPublisherPort {
    void publishWeatherAlert(UUID farmId, WeatherAlert alert);
    void publishDroughtTriggered(UUID farmId, DroughtCondition condition);
}
