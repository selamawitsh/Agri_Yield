package com.agriyield.farmservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.application.port.outgoing.FraudServicePort;
import com.agriyield.geospatialservice.grpc.GeospatialServiceGrpc;
import com.agriyield.geospatialservice.grpc.GeospatialServiceProto;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * GPS / boundary checks delegated to geospatial-service (replaces fraud stub).
 */
@Slf4j
@Primary
@Component
public class GeospatialBoundaryClient implements FraudServicePort {

    @GrpcClient("geospatial-service")
    private GeospatialServiceGrpc.GeospatialServiceBlockingStub geospatialStub;

    @Override
    public GpsVerificationResult verifyGpsConsistency(UUID farmId,
                                                    BigDecimal photoLat,
                                                    BigDecimal photoLng,
                                                    String farmGeoJsonPolygon) {
        log.info("gRPC: verifyFarmBoundary via geospatial farm={}", farmId);
        try {
            GeospatialServiceProto.BoundaryVerificationResponse response =
                geospatialStub.verifyFarmBoundary(
                    GeospatialServiceProto.VerifyBoundaryRequest.newBuilder()
                        .setFarmId(farmId.toString())
                        .setPhotoLat(photoLat.doubleValue())
                        .setPhotoLng(photoLng.doubleValue())
                        .build());
            return new GpsVerificationResult(
                response.getIsWithinBoundary(),
                response.getDistanceFromBoundaryM());
        } catch (Exception e) {
            log.warn("Geospatial boundary check failed, allowing upload: {}", e.getMessage());
            return new GpsVerificationResult(true, 0.0);
        }
    }
}
