package com.agriyield.weatherservice.presentation.controller;

import com.agriyield.weatherservice.application.port.incoming.WeatherServicePort;
import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.domain.model.WeatherAlert;
import com.agriyield.weatherservice.domain.model.WeatherReading;
import com.agriyield.weatherservice.domain.model.WeatherRisk;
import com.agriyield.weatherservice.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherServicePort weatherService;

    // WS-01: Get weather forecast
    @GetMapping("/forecast/{farmId}")
    public ResponseEntity<ApiResponse<List<WeatherReadingResponse>>> getForecast(
            @PathVariable UUID farmId,
            @RequestParam(defaultValue = "7") int days) {
        log.info("GET /weather/forecast/{}", farmId);
        List<WeatherReadingResponse> forecasts = weatherService.getForecast(farmId, days)
                .stream().map(WeatherReadingResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(forecasts));
    }

    // WS-02: Get rainfall data
    @GetMapping("/rainfall/{farmId}")
    public ResponseEntity<ApiResponse<List<WeatherReadingResponse>>> getRainfall(
            @PathVariable UUID farmId) {
        log.info("GET /weather/rainfall/{}", farmId);
        List<WeatherReadingResponse> data = weatherService.getRainfallData(farmId)
                .stream().map(WeatherReadingResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    // WS-03: Get drought status
    @GetMapping("/drought/{farmId}")
    public ResponseEntity<ApiResponse<DroughtStatusResponse>> getDroughtStatus(
            @PathVariable UUID farmId) {
        log.info("GET /weather/drought/{}", farmId);
        DroughtCondition condition = weatherService.getDroughtStatus(farmId);
        return ResponseEntity.ok(ApiResponse.success(DroughtStatusResponse.from(condition)));
    }

    // WS-05: Get current weather (used by AI service, investment service)
    @GetMapping("/current/{farmId}")
    public ResponseEntity<ApiResponse<WeatherReadingResponse>> getCurrentWeather(
            @PathVariable UUID farmId) {
        log.info("GET /weather/current/{}", farmId);
        WeatherReading reading = weatherService.getCurrentWeather(farmId);
        return ResponseEntity.ok(ApiResponse.success(WeatherReadingResponse.from(reading)));
    }

    // WS-07: Get weather risk score
    @GetMapping("/risk/{farmId}")
    public ResponseEntity<ApiResponse<WeatherRiskResponse>> getWeatherRisk(
            @PathVariable UUID farmId) {

        log.info("GET /weather/risk/{}", farmId);

        WeatherRisk risk = weatherService.calculateWeatherRiskScore(farmId);

        WeatherRiskResponse response = WeatherRiskResponse.builder()
                .farmId(farmId)
                .riskScore(risk.getScore())
                .rainfallRisk(risk.getRainfallRisk())
                .droughtRisk(risk.getDroughtRisk())
                .ndviVolatility(risk.getNdviVolatility())
                .riskLevel(WeatherRiskResponse.toRiskLevel(risk.getScore()))
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
    // WS-08: Get historical weather
    @GetMapping("/history/{farmId}")
    public ResponseEntity<ApiResponse<List<WeatherReadingResponse>>> getHistory(
            @PathVariable UUID farmId) {
        log.info("GET /weather/history/{}", farmId);
        List<WeatherReadingResponse> history = weatherService.getHistoricalWeather(farmId)
                .stream().map(WeatherReadingResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    // WS-09: Get weather alerts
    @GetMapping("/alerts/{farmId}")
    public ResponseEntity<ApiResponse<List<WeatherAlertResponse>>> getAlerts(
            @PathVariable UUID farmId) {
        log.info("GET /weather/alerts/{}", farmId);
        List<WeatherAlertResponse> alerts = weatherService.getWeatherAlerts(farmId)
                .stream().map(WeatherAlertResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(alerts));
    }

    // Manual trigger for testing
    @PostMapping("/fetch/{farmId}")
    public ResponseEntity<ApiResponse<String>> triggerWeatherFetch(
            @PathVariable UUID farmId) {

        log.info("POST /weather/fetch/{}", farmId);

        weatherService.fetchAndStoreWeather(farmId);

        return ResponseEntity.ok(
                ApiResponse.success("Weather fetch triggered"));
    }
}
