package com.agriyield.weatherservice.application.port.outgoing;

import com.agriyield.weatherservice.domain.model.WeatherReading;
import java.util.List;

public interface OpenWeatherClientPort {
    WeatherReading fetchCurrentWeather(double lat, double lng);
    List<WeatherReading> fetchForecast(double lat, double lng, int days);
}
