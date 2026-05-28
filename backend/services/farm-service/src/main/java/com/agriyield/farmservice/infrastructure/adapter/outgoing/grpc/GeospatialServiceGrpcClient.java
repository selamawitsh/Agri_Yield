package com.agriyield.farmservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.application.port.outgoing.GeospatialServicePort;
import com.agriyield.geospatialservice.grpc.GeospatialServiceGrpc;
import com.agriyield.geospatialservice.grpc.GeospatialServiceProto;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class GeospatialServiceGrpcClient implements GeospatialServicePort {

    @GrpcClient("geospatial-service")
    private GeospatialServiceGrpc.GeospatialServiceBlockingStub geospatialStub;

    @Override
    public PolygonValidation validatePolygon(String geoJsonPolygon) {
        log.info("gRPC: validatePolygon");
        try {
            GeospatialServiceProto.ValidatePolygonResponse response =
                geospatialStub.validatePolygon(
                    GeospatialServiceProto.ValidatePolygonRequest.newBuilder()
                        .setGeoJsonPolygon(geoJsonPolygon)
                        .build());
            log.info("gRPC: validatePolygon result valid={} msg={}",
                response.getValid(), response.getMessage());
            return new PolygonValidation(
                response.getValid(),
                response.getMessage(),
                BigDecimal.valueOf(response.getAreaHectares()),
                BigDecimal.valueOf(response.getCentroidLat()),
                BigDecimal.valueOf(response.getCentroidLng()));
        } catch (io.grpc.StatusRuntimeException e) {
            throw new RuntimeException(
                "Geospatial service error: " + e.getStatus().getDescription(), e);
        }
    }

    @Override
    public SpatialOverlap detectSpatialOverlap(UUID farmId, String geoJsonPolygon,
                                               BigDecimal centroidLat, BigDecimal centroidLng) {
        log.info("gRPC: detectSpatialOverlap farm={}", farmId);
        GeospatialServiceProto.DetectSpatialOverlapResponse response =
            geospatialStub.detectSpatialOverlap(
                GeospatialServiceProto.DetectSpatialOverlapRequest.newBuilder()
                    .setFarmId(farmId.toString())
                    .setGeoJsonPolygon(geoJsonPolygon)
                    .setCentroidLat(centroidLat.doubleValue())
                    .setCentroidLng(centroidLng.doubleValue())
                    .build());
        UUID conflicting = response.getConflictingFarmId().isBlank()
            ? null : UUID.fromString(response.getConflictingFarmId());
        return new SpatialOverlap(response.getHasOverlap(), response.getMessage(), conflicting);
    }

    @Override
    public void registerFarmPolygon(UUID farmId, String geoJsonPolygon,
                                    BigDecimal centroidLat, BigDecimal centroidLng,
                                    BigDecimal areaHectares) {
        log.info("gRPC: registerFarmPolygon farm={}", farmId);
        geospatialStub.registerFarmPolygon(
            GeospatialServiceProto.RegisterFarmPolygonRequest.newBuilder()
                .setFarmId(farmId.toString())
                .setGeoJsonPolygon(geoJsonPolygon)
                .setCentroidLat(centroidLat.doubleValue())
                .setCentroidLng(centroidLng.doubleValue())
                .setAreaHectares(areaHectares.doubleValue())
                .build());
    }
}
