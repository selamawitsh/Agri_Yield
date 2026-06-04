package com.agriyield.weatherservice.application.port.incoming;

import com.agriyield.weatherservice.domain.model.*;
import java.util.List;
import java.util.UUID;

public interface WeatherServicePort {

    List<WeatherReading> getForecast(UUID farmId, int days);

    List<WeatherReading> getRainfallData(UUID farmId);

    DroughtCondition getDroughtStatus(UUID farmId);

    WeatherReading getCurrentWeather(UUID farmId);

    WeatherRisk calculateWeatherRiskScore(UUID farmId); // ✅ FIXED

    List<WeatherReading> getHistoricalWeather(UUID farmId);

    List<WeatherAlert> getWeatherAlerts(UUID farmId);

    void fetchAndStoreWeather(UUID farmId);

    void analyzeDrought(UUID farmId);
}