package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.aiservice.grpc.*;
import com.agriyield.geospatialservice.application.port.outgoing.AiServicePort;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

/**
 * Geospatial-service gRPC CLIENT that calls ai-service.
 * Used in GeospatialServiceImpl weekly yield prediction job.
 */
@Slf4j
@Component
public class AiServiceGrpcClient implements AiServicePort {

    @GrpcClient("ai-service")
    private AiServiceGrpc.AiServiceBlockingStub aiServiceStub;

    @Override
    public YieldPrediction predictYield(YieldPredictionInput input) {
        log.info("Calling ai-service.PredictYield for farmId={}", input.farmId());
        try {
            YieldPredictionRequest request = YieldPredictionRequest.newBuilder()
                    .setFarmId(input.farmId())
                    .setCropType(input.cropType())
                    .setNdviPeak(input.ndviPeak())
                    .setNdviGrowthRate(input.ndviGrowthRate())
                    .setNdviCurrent(input.ndviCurrent())
                    .setNdviSmoothness(input.ndviSmoothness())
                    .setTotalRainfallMm(input.totalRainfallMm())
                    .setAvgTemperatureC(input.avgTemperatureC())
                    .setAltitudeM(input.altitudeM())
                    .setCropVarietyEncoded(input.cropVarietyEncoded())
                    .setFarmAreaHa(input.farmAreaHa())
                    .setInputQualityEncoded(input.inputQualityEncoded())
                    .setDaysSincePlanting(input.daysSincePlanting())
                    .setHistoricalZoneYield(input.historicalZoneYield())
                    .setModelVersion("rule-based-v1.0")
                    .build();

            YieldPredictionResponse response = aiServiceStub.predictYield(request);

            return new YieldPrediction(
                    response.getPredictedYieldQuintalsPerHa(),
                    response.getLowerBound(),
                    response.getUpperBound(),
                    response.getConfidencePct(),
                    response.getModelVersion()
            );
        } catch (StatusRuntimeException e) {
            log.error("ai-service PredictYield gRPC failed for farm={}: {}", input.farmId(), e.getStatus());
            // Return a safe fallback prediction so the geospatial scheduler doesn't crash
            return new YieldPrediction(
                    input.historicalZoneYield(),
                    input.historicalZoneYield() * 0.8,
                    input.historicalZoneYield() * 1.2,
                    50,
                    "fallback"
            );
        }
    }
}
