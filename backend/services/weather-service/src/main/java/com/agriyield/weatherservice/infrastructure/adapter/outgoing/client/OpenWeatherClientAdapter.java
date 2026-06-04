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
                    .fromHttpUrl(baseUrl + "/weather")
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
                    .fromHttpUrl(baseUrl + "/forecast")
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

        if (response == null) {
            return buildFallbackReading(lat, lng, ForecastType.ACTUAL);
        }

        Map<String, Object> main =
                (Map<String, Object>) response.get("main");

        if (main == null) {
            return buildFallbackReading(lat, lng, ForecastType.ACTUAL);
        }

        double temp =
                ((Number) main.getOrDefault("temp", 20.0)).doubleValue();

        double humidity =
                ((Number) main.getOrDefault("humidity", 60.0)).doubleValue();

        double rain = 0.0;

        if (response.containsKey("rain")) {
            Map<String, Object> rainMap =
                    (Map<String, Object>) response.get("rain");

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

        List<Map<String, Object>> daily = (List<Map<String, Object>>) response.get("list");
        if (daily == null) return readings;

        int limit = Math.min(days, daily.size());
        for (int i = 0; i < limit; i++) {
            Map<String, Object> day = daily.get(i);
            // OpenWeather /forecast returns "list" items with "main" block
            Map<String, Object> mainBlock = (Map<String, Object>) day.get("main");
            double tempDay = mainBlock != null
                ? ((Number) mainBlock.getOrDefault("temp", 20.0)).doubleValue()
                : 20.0;
            double humidity = mainBlock != null
                ? ((Number) mainBlock.getOrDefault("humidity", 60.0)).doubleValue()
                : 60.0;
            // rain is nested as {"rain": {"3h": value}}
            double rain = 0.0;
            if (day.containsKey("rain")) {
                Object rainObj = day.get("rain");
                if (rainObj instanceof Map) {
                    Map<String, Object> rainMap = (Map<String, Object>) rainObj;
                    rain = rainMap.containsKey("3h")
                        ? ((Number) rainMap.get("3h")).doubleValue() : 0.0;
                } else if (rainObj instanceof Number) {
                    rain = ((Number) rainObj).doubleValue();
                }
            }

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
