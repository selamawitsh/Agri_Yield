package com.agriyield.farmservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.farmservice.application.port.outgoing.AgriScoreRepositoryPort;
import com.agriyield.farmservice.application.port.outgoing.CropCycleRepositoryPort;
import com.agriyield.farmservice.application.port.outgoing.FarmRepositoryPort;
import com.agriyield.farmservice.domain.model.AgriScore;
import com.agriyield.farmservice.domain.model.CropCycle;
import com.agriyield.farmservice.domain.model.Farm;
import com.agriyield.farmservice.grpc.FarmServiceGrpc;
import com.agriyield.farmservice.grpc.FarmServiceProto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class FarmGrpcService extends FarmServiceGrpc.FarmServiceImplBase {

    private final FarmRepositoryPort farmRepository;
    private final CropCycleRepositoryPort cropCycleRepository;
    private final AgriScoreRepositoryPort agriScoreRepository;

    @Override
    public void getFarmById(FarmIdRequest request,
                            StreamObserver<FarmResponse> responseObserver) {
        log.info("gRPC getFarmById: {}", request.getFarmId());
        try {
            UUID farmId = UUID.fromString(request.getFarmId());
            Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found: " + farmId));

            FarmResponse response = FarmResponse.newBuilder()
                .setFarmId(farm.getId().toString())
                .setFarmerId(farm.getFarmerId().toString())
                .setCropType(farm.getCropType().getValue())
                .setAreaHectares(farm.getAreaHectares().doubleValue())
                .setStatus(farm.getStatus().getValue())
                .setKebeleCode(farm.getKebeleCode())
                .setRegion(farm.getRegion())
                .setGpsCentroidLat(farm.getGpsCentroidLat().doubleValue())
                .setGpsCentroidLng(farm.getGpsCentroidLng().doubleValue())
                .setSatelliteVerified(farm.getSatelliteVerified())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID").asRuntimeException());
        } catch (RuntimeException e) {
            log.error("gRPC getFarmById failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getFarmContext(FarmIdRequest request,
                               StreamObserver<FarmContextResponse> responseObserver) {
        log.info("gRPC getFarmContext: {}", request.getFarmId());
        try {
            UUID farmId = UUID.fromString(request.getFarmId());
            Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found: " + farmId));

            CropCycle activeCycle = cropCycleRepository
                .findActiveByFarmId(farmId).orElse(null);

            AgriScore latestScore = agriScoreRepository
                .findLatestByFarmerId(farm.getFarmerId()).orElse(null);

            FarmContextResponse.Builder builder = FarmContextResponse.newBuilder()
                .setFarmId(farm.getId().toString())
                .setFarmerId(farm.getFarmerId().toString())
                .setCropType(farm.getCropType().getValue())
                .setAreaHectares(farm.getAreaHectares().doubleValue())
                .setStatus(farm.getStatus().getValue())
                .setRegion(farm.getRegion())
                .setKebeleCode(farm.getKebeleCode())
                .setGpsCentroidLat(farm.getGpsCentroidLat().doubleValue())
                .setGpsCentroidLng(farm.getGpsCentroidLng().doubleValue())
                .setAgriScore(latestScore != null ? latestScore.getScore() : 50);

            if (activeCycle != null) {
                builder.setCropCycleId(activeCycle.getId().toString())
                       .setSeasonName(activeCycle.getSeasonName())
                       .setCropCycleStatus(activeCycle.getStatus().getValue());
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID").asRuntimeException());
        } catch (RuntimeException e) {
            log.error("gRPC getFarmContext failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void verifyFarmBoundary(VerifyBoundaryRequest request,
                                   StreamObserver<BoundaryVerificationResponse> responseObserver) {
        log.info("gRPC verifyFarmBoundary: {}", request.getFarmId());
        try {
            UUID farmId = UUID.fromString(request.getFarmId());
            Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found: " + farmId));

            // Stub boundary verification — geospatial-service handles real verification
            BoundaryVerificationResponse response = BoundaryVerificationResponse.newBuilder()
                .setIsWithinBoundary(true)
                .setDistanceFromBoundaryM(0.0)
                .setGeoJsonPolygon(farm.getGeoJsonPolygon() != null
                    ? farm.getGeoJsonPolygon() : "")
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (RuntimeException e) {
            log.error("gRPC verifyFarmBoundary failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
