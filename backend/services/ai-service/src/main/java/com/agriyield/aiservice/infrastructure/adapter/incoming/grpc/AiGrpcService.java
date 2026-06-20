package com.agriyield.aiservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.aiservice.application.port.incoming.AiServicePort;
import com.agriyield.aiservice.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

/**
 * gRPC server for ai-service.
 * Called by:
 *   - geospatial-service -> PredictYield
 *   - fraud-service -> ScoreFraudRisk
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AiGrpcService extends AiServiceGrpc.AiServiceImplBase {

    private final AiServicePort aiServicePort;

    @Override
    public void predictYield(YieldPredictionRequest request,
                             StreamObserver<YieldPredictionResponse> responseObserver) {
        log.info("gRPC PredictYield called for farmId={}", request.getFarmId());
        try {
            AiServicePort.YieldPredictionInput input = new AiServicePort.YieldPredictionInput(
                    request.getFarmId(),
                    request.getCropType(),
                    request.getNdviPeak(),
                    request.getNdviGrowthRate(),
                    request.getNdviCurrent(),
                    request.getNdviSmoothness(),
                    request.getTotalRainfallMm(),
                    request.getAvgTemperatureC(),
                    request.getAltitudeM(),
                    request.getCropVarietyEncoded(),
                    request.getFarmAreaHa(),
                    request.getInputQualityEncoded(),
                    request.getDaysSincePlanting(),
                    request.getHistoricalZoneYield(),
                    request.getModelVersion()
            );

            AiServicePort.YieldPredictionResult result = aiServicePort.predictYield(input);

            YieldPredictionResponse response = YieldPredictionResponse.newBuilder()
                    .setPredictedYieldQuintalsPerHa(result.predictedYieldQuintalsPerHa())
                    .setLowerBound(result.lowerBound())
                    .setUpperBound(result.upperBound())
                    .setConfidencePct(result.confidencePct())
                    .setModelVersion(result.modelVersion())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC PredictYield failed: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void scoreFraudRisk(FraudRiskRequest request,
                               StreamObserver<FraudRiskResponse> responseObserver) {
        log.info("gRPC ScoreFraudRisk called for entityId={}", request.getEntityId());
        try {
            AiServicePort.FraudRiskResult result = aiServicePort.scoreFraudRisk(
                    request.getEntityId(),
                    request.getEntityType(),
                    request.getEventType(),
                    request.getEventPayloadJson()
            );

            FraudRiskResponse response = FraudRiskResponse.newBuilder()
                    .setFraudProbability(result.fraudProbability())
                    .setModelVersion(result.modelVersion())
                    .setRiskLevel(result.riskLevel())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC ScoreFraudRisk failed: {}", e.getMessage(), e);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }
}