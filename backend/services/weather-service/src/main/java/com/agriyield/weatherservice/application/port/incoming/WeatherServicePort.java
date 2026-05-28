package com.agriyield.weatherservice.application.port.incoming;

import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.domain.model.WeatherAlert;
import com.agriyield.weatherservice.domain.model.WeatherReading;

import java.util.List;
import java.util.UUID;

public interface WeatherServicePort {

    // WS-01: Get forecast for a farm
    List<WeatherReading> getForecast(UUID farmId, int days);

    // WS-02: Get rainfall data for a farm
    List<WeatherReading> getRainfallData(UUID farmId);

    // WS-03: Get drought status
    DroughtCondition getDroughtStatus(UUID farmId);

    // WS-05: Get latest NDVI-adjacent weather data
    WeatherReading getCurrentWeather(UUID farmId);

    // WS-07: Calculate weather risk score
    double calculateWeatherRiskScore(UUID farmId);

    // WS-08: Get historical weather data
    List<WeatherReading> getHistoricalWeather(UUID farmId);

    // WS-09: Get weather alerts for a farm
    List<WeatherAlert> getWeatherAlerts(UUID farmId);

    // Internal: fetch and store weather from OpenWeather API
    void fetchAndStoreWeather(UUID farmId, double lat, double lng);

    // Internal: run drought analysis for a farm
    void analyzeDrought(UUID farmId);
}
