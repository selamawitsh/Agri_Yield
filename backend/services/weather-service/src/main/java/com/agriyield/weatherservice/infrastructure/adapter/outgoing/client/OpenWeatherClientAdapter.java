package com.agriyield.weatherservice.infrastructure.adapter.outgoing.client;

import com.agriyield.weatherservice.application.port.outgoing.OpenWeatherClientPort;
import com.agriyield.weatherservice.domain.enums.ForecastType;
import com.agriyield.weatherservice.domain.model.WeatherReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenWeatherClientAdapter implements OpenWeatherClientPort {

    private final RestTemplate restTemplate;

    @Value("${app.openweather.api-key}")
    private String apiKey;

    @Value("${app.openweather.base-url}")
    private String baseUrl;

    @Override
    public WeatherReading fetchCurrentWeather(double lat, double lng) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/onecall")
                    .queryParam("lat", lat)
                    .queryParam("lon", lng)
                    .queryParam("exclude", "minutely,hourly,daily,alerts")
                    .queryParam("units", "metric")
                    .queryParam("appid", apiKey)
                    .toUriString();

            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            return parseCurrentWeather(response, lat, lng);
        } catch (Exception e) {
            log.error("Failed to fetch current weather: {}", e.getMessage());
            return buildFallbackReading(lat, lng, ForecastType.ACTUAL);
        }
    }

    @Override
    public List<WeatherReading> fetchForecast(double lat, double lng, int days) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(baseUrl + "/onecall")
                    .queryParam("lat", lat)
                    .queryParam("lon", lng)
                    .queryParam("exclude", "current,minutely,hourly,alerts")
                    .queryParam("units", "metric")
                    .queryParam("appid", apiKey)
                    .toUriString();

            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            return parseForecast(response, lat, lng, days);
        } catch (Exception e) {
            log.error("Failed to fetch forecast: {}", e.getMessage());
            return List.of();
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherReading parseCurrentWeather(Map<?, ?> response, double lat, double lng) {
        if (response == null) return buildFallbackReading(lat, lng, ForecastType.ACTUAL);
        Map<String, Object> current = (Map<String, Object>) response.get("current");
        if (current == null) return buildFallbackReading(lat, lng, ForecastType.ACTUAL);

        double temp = ((Number) current.getOrDefault("temp", 20.0)).doubleValue();
        double humidity = ((Number) current.getOrDefault("humidity", 60.0)).doubleValue();

        // Rain is nested: current.rain.1h
        double rain = 0.0;
        if (current.containsKey("rain")) {
            Map<String, Object> rainMap = (Map<String, Object>) current.get("rain");
            if (rainMap != null && rainMap.containsKey("1h")) {
                rain = ((Number) rainMap.get("1h")).doubleValue();
            }
        }

        return WeatherReading.builder()
                .gpsLat(BigDecimal.valueOf(lat))
                .gpsLng(BigDecimal.valueOf(lng))
                .temperatureC(BigDecimal.valueOf(temp))
                .rainfallMm(BigDecimal.valueOf(rain))
                .humidityPct(BigDecimal.valueOf(humidity))
                .forecastType(ForecastType.ACTUAL)
                .recordedDate(LocalDate.now())
                .fetchedAt(OffsetDateTime.now())
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<WeatherReading> parseForecast(Map<?, ?> response, double lat, double lng, int days) {
        List<WeatherReading> readings = new ArrayList<>();
        if (response == null) return readings;

        List<Map<String, Object>> daily = (List<Map<String, Object>>) response.get("daily");
        if (daily == null) return readings;

        int limit = Math.min(days, daily.size());
        for (int i = 0; i < limit; i++) {
            Map<String, Object> day = daily.get(i);
            Map<String, Object> temp = (Map<String, Object>) day.get("temp");
            double tempDay = temp != null ? ((Number) temp.getOrDefault("day", 20.0)).doubleValue() : 20.0;
            double rain = day.containsKey("rain") ? ((Number) day.get("rain")).doubleValue() : 0.0;
            double humidity = ((Number) day.getOrDefault("humidity", 60.0)).doubleValue();

            readings.add(WeatherReading.builder()
                    .gpsLat(BigDecimal.valueOf(lat))
                    .gpsLng(BigDecimal.valueOf(lng))
                    .temperatureC(BigDecimal.valueOf(tempDay))
                    .rainfallMm(BigDecimal.valueOf(rain))
                    .humidityPct(BigDecimal.valueOf(humidity))
                    .forecastType(ForecastType.FORECAST)
                    .forecastHorizonDays(i + 1)
                    .recordedDate(LocalDate.now().plusDays(i + 1))
                    .fetchedAt(OffsetDateTime.now())
                    .build());
        }
        return readings;
    }

    private WeatherReading buildFallbackReading(double lat, double lng, ForecastType type) {
        return WeatherReading.builder()
                .gpsLat(BigDecimal.valueOf(lat))
                .gpsLng(BigDecimal.valueOf(lng))
                .temperatureC(BigDecimal.valueOf(20.0))
                .rainfallMm(BigDecimal.valueOf(0.0))
                .humidityPct(BigDecimal.valueOf(60.0))
                .forecastType(type)
                .recordedDate(LocalDate.now())
                .fetchedAt(OffsetDateTime.now())
                .build();
    }
}
