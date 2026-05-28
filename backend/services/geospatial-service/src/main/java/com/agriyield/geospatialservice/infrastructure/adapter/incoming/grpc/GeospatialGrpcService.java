package com.agriyield.geospatialservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;
import com.agriyield.geospatialservice.grpc.GeospatialServiceGrpc;
import com.agriyield.geospatialservice.grpc.GeospatialServiceProto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GeospatialGrpcService
    extends GeospatialServiceGrpc.GeospatialServiceImplBase {

    private final GeospatialServicePort geospatialService;

    /** SRS: StartMonitoring — called by farm-service when farm.registered event arrives */
    @Override
    public void startMonitoring(FarmIdRequest request,
                                 StreamObserver<BooleanResponse> responseObserver) {
        log.info("gRPC startMonitoring farmId={}", request.getFarmId());
        try {
            boolean result = geospatialService
                .startMonitoring(UUID.fromString(request.getFarmId()));
            responseObserver.onNext(BooleanResponse.newBuilder()
                .setSuccess(result).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC startMonitoring failed", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /** SRS: GetLatestNdvi — called by investment-service, ai-service */
    @Override
    public void getLatestNdvi(FarmIdRequest request,
                               StreamObserver<NdviResponse> responseObserver) {
        log.info("gRPC getLatestNdvi farmId={}", request.getFarmId());
        try {
            NdviReading reading = geospatialService
                .getLatestNdvi(UUID.fromString(request.getFarmId()));
            responseObserver.onNext(NdviResponse.newBuilder()
                .setFarmId(reading.getFarmId().toString())
                .setNdviValue(reading.getNdviValue())
                .setCloudCoverage(reading.getCloudCoverage())
                .setHealthStatus(reading.getHealthStatus())
                .setRecordedDate(reading.getRecordedDate().toString())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getLatestNdvi failed", e);
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /** SRS: GetNdviTimeSeries — called by investment-service for charts */
    @Override
    public void getNdviTimeSeries(NdviTimeSeriesRequest request,
                                   StreamObserver<NdviTimeSeriesResponse> responseObserver) {
        log.info("gRPC getNdviTimeSeries farmId={} days={}",
            request.getFarmId(), request.getLimitDays());
        try {
            List<NdviReading> readings = geospatialService.getNdviTimeSeries(
                UUID.fromString(request.getFarmId()), request.getLimitDays());

            List<NdviDataPoint> points = readings.stream()
                .map(r -> NdviDataPoint.newBuilder()
                    .setDate(r.getRecordedDate().toString())
                    .setNdviValue(r.getNdviValue())
                    .setCloudCoverage(r.getCloudCoverage())
                    .build())
                .collect(Collectors.toList());

            responseObserver.onNext(NdviTimeSeriesResponse.newBuilder()
                .setFarmId(request.getFarmId())
                .addAllReadings(points)
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getNdviTimeSeries failed", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /** SRS: PredictHarvestReadiness — called by offtaker-service */
    @Override
    public void predictHarvestReadiness(FarmIdRequest request,
                                         StreamObserver<HarvestReadinessResponse> responseObserver) {
        log.info("gRPC predictHarvestReadiness farmId={}", request.getFarmId());
        try {
            GeospatialServicePort.HarvestReadinessResult result =
                geospatialService.predictHarvestReadiness(
                    UUID.fromString(request.getFarmId()));

            HarvestReadinessResponse.Builder builder =
                HarvestReadinessResponse.newBuilder()
                    .setIsReady(result.ready())
                    .setCurrentNdvi(result.currentNdvi())
                    .setPeakNdvi(result.peakNdvi())
                    .setReadinessSignal(result.readinessSignal());

            if (result.estimatedDateFrom() != null)
                builder.setEstimatedDateFrom(result.estimatedDateFrom());
            if (result.estimatedDateTo() != null)
                builder.setEstimatedDateTo(result.estimatedDateTo());

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC predictHarvestReadiness failed", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /** SRS: GetFarmContext — called by offtaker-service for farm browsing */
    @Override
    public void getFarmContext(FarmIdRequest request,
                                StreamObserver<FarmContextResponse> responseObserver) {
        log.info("gRPC getFarmContext farmId={}", request.getFarmId());
        try {
            GeospatialServicePort.FarmContext ctx =
                geospatialService.getFarmContext(
                    UUID.fromString(request.getFarmId()));

            responseObserver.onNext(FarmContextResponse.newBuilder()
                .setFarmId(ctx.farmId())
                .setFarmerId(ctx.farmerId())
                .setCropType(ctx.cropType())
                .setAreaHectares(ctx.areaHectares())
                .setRegion(ctx.region())
                .setKebeleCode(ctx.kebeleCode())
                .setGpsCentroidLat(ctx.gpsCentroidLat())
                .setGpsCentroidLng(ctx.gpsCentroidLng())
                .setAgriScore(ctx.agriScore())
                .setCropCycleId(ctx.cropCycleId() != null ? ctx.cropCycleId() : "")
                .setSeasonName(ctx.seasonName() != null ? ctx.seasonName() : "")
                .setCropCycleStatus(ctx.cropCycleStatus() != null ? ctx.cropCycleStatus() : "")
                .setCurrentNdvi(ctx.currentNdvi())
                .setNdviHealthStatus(ctx.ndviHealthStatus())
                .setPredictedYieldMeanQuintals(ctx.predictedYieldMeanQuintals())
                .setYieldConfidencePct(ctx.yieldConfidencePct())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getFarmContext failed", e);
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void registerFarmPolygon(RegisterFarmPolygonRequest request,
                                     StreamObserver<RegisterFarmPolygonResponse> responseObserver) {
        log.info("gRPC registerFarmPolygon farmId={}", request.getFarmId());
        try {
            GeospatialServicePort.RegisterPolygonResult result = geospatialService
                .registerFarmPolygon(
                    UUID.fromString(request.getFarmId()),
                    request.getGeoJsonPolygon(),
                    request.getCentroidLat(),
                    request.getCentroidLng(),
                    request.getAreaHectares() > 0 ? request.getAreaHectares() : null);
            geospatialService.startMonitoring(UUID.fromString(request.getFarmId()));
            responseObserver.onNext(RegisterFarmPolygonResponse.newBuilder()
                .setSuccess(result.success())
                .setMessage(result.message())
                .setAreaHectares(result.areaHectares())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC registerFarmPolygon failed", e);
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void validatePolygon(ValidatePolygonRequest request,
                               StreamObserver<ValidatePolygonResponse> responseObserver) {
        log.info("gRPC validatePolygon");
        try {
            GeospatialServicePort.PolygonValidationResult result =
                geospatialService.validatePolygon(request.getGeoJsonPolygon());
            responseObserver.onNext(ValidatePolygonResponse.newBuilder()
                .setValid(result.valid())
                .setMessage(result.message())
                .setAreaHectares(result.areaHectares())
                .setCentroidLat(result.centroidLat())
                .setCentroidLng(result.centroidLng())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void calculateFarmArea(FarmIdRequest request,
                                   StreamObserver<CalculateFarmAreaResponse> responseObserver) {
        log.info("gRPC calculateFarmArea farmId={}", request.getFarmId());
        try {
            GeospatialServicePort.FarmAreaResult result = geospatialService
                .calculateFarmArea(UUID.fromString(request.getFarmId()));
            responseObserver.onNext(CalculateFarmAreaResponse.newBuilder()
                .setFarmId(request.getFarmId())
                .setAreaHectares(result.areaHectares())
                .setAreaSqKm(result.areaSqKm())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void detectSpatialOverlap(DetectSpatialOverlapRequest request,
                                      StreamObserver<DetectSpatialOverlapResponse> responseObserver) {
        log.info("gRPC detectSpatialOverlap farmId={}", request.getFarmId());
        try {
            GeospatialServicePort.SpatialOverlapResult result = geospatialService
                .detectSpatialOverlap(
                    UUID.fromString(request.getFarmId()),
                    request.getGeoJsonPolygon(),
                    request.getCentroidLat(),
                    request.getCentroidLng());
            DetectSpatialOverlapResponse.Builder builder =
                DetectSpatialOverlapResponse.newBuilder()
                    .setHasOverlap(result.hasOverlap())
                    .setOverlapPercentage(result.overlapPercentage())
                    .setMessage(result.message());
            if (result.conflictingFarmId() != null) {
                builder.setConflictingFarmId(result.conflictingFarmId().toString());
            }
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /** SRS: VerifyFarmBoundary — called by fraud-service for photo GPS check */
    @Override
    public void verifyFarmBoundary(VerifyBoundaryRequest request,
                                    StreamObserver<BoundaryVerificationResponse> responseObserver) {
        log.info("gRPC verifyFarmBoundary farmId={}", request.getFarmId());
        try {
            GeospatialServicePort.BoundaryVerificationResult result =
                geospatialService.verifyFarmBoundary(
                    UUID.fromString(request.getFarmId()),
                    request.getPhotoLat(),
                    request.getPhotoLng());

            responseObserver.onNext(BoundaryVerificationResponse.newBuilder()
                .setIsWithinBoundary(result.isWithinBoundary())
                .setDistanceFromBoundaryM(result.distanceFromBoundaryM())
                .setGeoJsonPolygon(result.geoJsonPolygon() != null
                    ? result.geoJsonPolygon() : "")
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC verifyFarmBoundary failed", e);
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
