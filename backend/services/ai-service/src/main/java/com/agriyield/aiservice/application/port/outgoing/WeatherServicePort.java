package com.agriyield.aiservice.application.port.outgoing;

public interface WeatherServicePort {

    WeatherData getCurrentWeather(String farmId);

    record WeatherData(
            double temperatureC,
            double rainfallMm,
            double humidity,
            String forecastSummary
    ) {}
}