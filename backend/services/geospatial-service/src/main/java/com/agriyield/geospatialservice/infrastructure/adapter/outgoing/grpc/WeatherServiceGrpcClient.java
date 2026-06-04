package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.geospatialservice.application.port.outgoing.WeatherServicePort;
import com.agriyield.weatherservice.grpc.WeatherServiceGrpc;
import com.agriyield.weatherservice.grpc.WeatherServiceProto.FarmIdRequest;
import com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherForecastRequest;
import com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherResponse;
import com.agriyield.weatherservice.grpc.WeatherServiceProto.ForecastResponse;
import com.agriyield.weatherservice.grpc.WeatherServiceProto.DroughtStatusResponse;
import com.agriyield.weatherservice.grpc.WeatherServiceProto.WeatherRiskResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Real gRPC client for WeatherService (port 9090).
 *
 * Calls three RPCs defined in weather_service.proto to build a WeatherContext:
 *
 *   GetForecast(days=30)   → sums rainfall_mm and averages temperature_c
 *                            across all forecast entries to get seasonal totals
 *   GetDroughtStatus       → maps consecutive_dry_days to a 0–100 drought risk score
 *   GetWeatherRisk         → provides the risk_score used as ndvi_volatility proxy
 *
 * Falls back to Ethiopian highland averages ONLY if the gRPC call fails.
 */
@Slf4j
@Component
public class WeatherServiceGrpcClient implements WeatherServicePort {

    @GrpcClient("weather-service")
    private WeatherServiceGrpc.WeatherServiceBlockingStub weatherStub;

    @Override
    public WeatherContext getWeatherContext(UUID farmId) {
        log.info("gRPC → weather-service: getWeatherContext farmId={}", farmId);

        FarmIdRequest farmIdRequest = FarmIdRequest.newBuilder()
                .setFarmId(farmId.toString())
                .build();

        try {
            // ── Call 1: 30-day forecast → sum rainfall, average temperature ──────
            ForecastResponse forecastResponse = weatherStub.getForecast(
                    WeatherForecastRequest.newBuilder()
                            .setFarmId(farmId.toString())
                            .setDays(30)
                            .build()
            );

            List<WeatherResponse> forecasts = forecastResponse.getForecastsList();

            double totalRainfallMm = 0.0;
            double avgTempC        = 18.0; // Ethiopian highland fallback
            int    dryDayCount     = 0;

            if (!forecasts.isEmpty()) {
                double tempSum = 0.0;
                for (WeatherResponse day : forecasts) {
                    totalRainfallMm += day.getRainfallMm();
                    tempSum         += day.getTemperatureC();
                    if (day.getIsDryDay()) dryDayCount++;
                }
                avgTempC = tempSum / forecasts.size();
            }

            // ── Call 2: drought status → drought risk score 0–100 ────────────────
            DroughtStatusResponse droughtResponse = weatherStub.getDroughtStatus(farmIdRequest);

            int droughtRiskScore = 0;
            if (droughtResponse.getIsTriggered()) {
                // Triggered drought: score starts at 60, grows with consecutive dry days
                int consecutive = droughtResponse.getConsecutiveDryDays();
                int threshold   = droughtResponse.getDroughtThresholdDays();
                // Score: 60 baseline + up to 40 more based on how far past threshold
                droughtRiskScore = threshold > 0
                        ? Math.min(100, 60 + (int)((consecutive - threshold) * 2.5))
                        : 60;
            } else {
                // Not triggered: light score based on dry day count in forecast
                int threshold = droughtResponse.getDroughtThresholdDays();
                int consecutive = droughtResponse.getConsecutiveDryDays();
                droughtRiskScore = threshold > 0
                        ? Math.min(55, (consecutive * 100) / threshold)
                        : (dryDayCount * 3); // fallback: 3 points per dry day
            }

            // ── Call 3: weather risk score → use as ndvi_volatility proxy ─────────
            WeatherRiskResponse riskResponse = weatherStub.getWeatherRisk(farmIdRequest);
            double ndviVolatility = riskResponse.getRiskScore() / 100.0; // normalise to 0–1

            log.info("gRPC ← weather-service: farmId={} rainfall={}mm avgTemp={}°C " +
                            "droughtRisk={} ndviVolatility={} riskLevel={}",
                    farmId,
                    String.format("%.1f", totalRainfallMm),
                    String.format("%.1f", avgTempC),
                    droughtRiskScore,
                    String.format("%.3f", ndviVolatility),
                    riskResponse.getRiskLevel());

            return new WeatherContext(
                    totalRainfallMm,
                    avgTempC,
                    droughtRiskScore,
                    ndviVolatility
            );

        } catch (StatusRuntimeException e) {
            log.error("gRPC: getWeatherContext FAILED for farmId={} — status={} message={}",
                    farmId, e.getStatus().getCode(), e.getMessage());
            log.warn("gRPC: Using Ethiopian highland fallback for farmId={}. " +
                    "Ensure weather-service is running on port 9090.", farmId);

            // Ethiopian highland averages — used only when weather-service is unreachable
            return new WeatherContext(800.0, 18.0, 0, 0.1);

        } catch (Exception e) {
            log.error("gRPC: getWeatherContext unexpected error for farmId={}: {}",
                    farmId, e.getMessage());
            return new WeatherContext(800.0, 18.0, 0, 0.1);
        }
    }
}