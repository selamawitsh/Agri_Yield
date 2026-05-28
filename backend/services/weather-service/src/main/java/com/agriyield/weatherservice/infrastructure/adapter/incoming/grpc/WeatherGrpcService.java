package com.agriyield.weatherservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.weatherservice.application.port.incoming.WeatherServicePort;
import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.domain.model.WeatherReading;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import com.agriyield.weatherservice.grpc.WeatherServiceGrpc;

import java.util.UUID;

/**
 * gRPC server — exposes WeatherService methods to other microservices.
 * SRS gRPC definition (Section 4.4.3):
 *   GetCurrentWeather, GetForecast, GetDroughtStatus, GetWeatherRisk
 *
 * Wire generated proto base class when weather_service.proto is compiled.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class WeatherGrpcService extends WeatherServiceGrpc.WeatherServiceImplBase {

    private final WeatherServicePort weatherService;

    public WeatherReading getCurrentWeather(UUID farmId) {
        log.info("gRPC GetCurrentWeather farmId={}", farmId);
        return weatherService.getCurrentWeather(farmId);
    }

    public DroughtCondition getDroughtStatus(UUID farmId) {
        log.info("gRPC GetDroughtStatus farmId={}", farmId);
        return weatherService.getDroughtStatus(farmId);
    }

    public double getWeatherRisk(UUID farmId) {
        log.info("gRPC GetWeatherRisk farmId={}", farmId);
        return weatherService.calculateWeatherRiskScore(farmId);
    }
}
