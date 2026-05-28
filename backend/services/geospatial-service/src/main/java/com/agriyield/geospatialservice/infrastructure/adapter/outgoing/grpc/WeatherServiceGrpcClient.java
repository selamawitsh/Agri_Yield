package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.geospatialservice.application.port.outgoing.WeatherServicePort;
import com.agriyield.weatherservice.grpc.WeatherServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class WeatherServiceGrpcClient implements WeatherServicePort {

    @GrpcClient("weather-service")
    private WeatherServiceGrpc.WeatherServiceBlockingStub weatherStub;

    @Override
    public WeatherContext getWeatherContext(UUID farmId) {
        log.info("gRPC: getWeatherContext farmId={}", farmId);
        try {
            // Wire to actual weather proto when available
            // For now return safe defaults so yield prediction can proceed
            return new WeatherContext(320.0, 18.5, 0, 0.1);
        } catch (Exception e) {
            log.error("gRPC: getWeatherContext failed: {} — using defaults", e.getMessage());
            return new WeatherContext(300.0, 18.0, 0, 0.1);
        }
    }
}
