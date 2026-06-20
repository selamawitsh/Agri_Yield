package com.agriyield.aiservice.infrastructure.adapter.outgoing.weather;

import com.agriyield.aiservice.application.port.outgoing.WeatherServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WeatherServiceAdapter implements WeatherServicePort {

    @Override
    public WeatherData getCurrentWeather(String farmId) {
        // TODO: Implement actual weather API call
        log.warn("Using default weather data for farm: {}. Real implementation needed!", farmId);
        
        return new WeatherData(
            25.0,                    // temperatureC (default 25°C)
            0.0,                     // rainfallMm (default 0mm)
            65.0,                    // humidity (default 65%)
            "Clear skies"            // forecastSummary
        );
    }
}
