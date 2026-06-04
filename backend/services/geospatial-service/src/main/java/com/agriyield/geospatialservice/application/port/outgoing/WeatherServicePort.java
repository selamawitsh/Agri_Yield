package com.agriyield.geospatialservice.application.port.outgoing;

import java.util.UUID;

public interface WeatherServicePort {

    record WeatherContext(
        double totalRainfallMm,
        double avgTempC,
        int consecutiveDryDays,
        double weatherRiskScore

    ) {}

    WeatherContext getWeatherContext(UUID farmId);
}
