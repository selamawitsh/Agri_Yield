package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.grpc.FarmServiceGrpc;
import com.agriyield.farmservice.grpc.FarmServiceProto.*;
import com.agriyield.geospatialservice.application.port.outgoing.FarmServicePort;
import com.agriyield.geospatialservice.domain.exception.FarmNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class FarmServiceGrpcClient implements FarmServicePort {

    @GrpcClient("farm-service")
    private FarmServiceGrpc.FarmServiceBlockingStub farmStub;

    @Override
    public FarmInfo getFarmById(UUID farmId) {
        log.info("gRPC: getFarmById farmId={}", farmId);
        try {
            FarmResponse r = farmStub.getFarmById(
                FarmIdRequest.newBuilder()
                    .setFarmId(farmId.toString())
                    .build());
            return new FarmInfo(
                r.getFarmId(), r.getFarmerId(), r.getCropType(),
                r.getAreaHectares(), r.getStatus(), r.getRegion(),
                r.getKebeleCode(), r.getGpsCentroidLat(), r.getGpsCentroidLng(),
                r.getSatelliteVerified(), 50, null, null, null
            );
        } catch (Exception e) {
            log.error("gRPC: getFarmById failed: {}", e.getMessage());
            throw new FarmNotFoundException(farmId.toString());
        }
    }

    @Override
    public FarmInfo getFarmContext(UUID farmId) {
        log.info("gRPC: getFarmContext farmId={}", farmId);
        try {
            FarmContextResponse r = farmStub.getFarmContext(
                FarmIdRequest.newBuilder()
                    .setFarmId(farmId.toString())
                    .build());
            return new FarmInfo(
                r.getFarmId(), r.getFarmerId(), r.getCropType(),
                r.getAreaHectares(), r.getStatus(), r.getRegion(),
                r.getKebeleCode(), r.getGpsCentroidLat(), r.getGpsCentroidLng(),
                true, r.getAgriScore(),
                r.getCropCycleId(), r.getSeasonName(), r.getCropCycleStatus()
            );
        } catch (Exception e) {
            log.error("gRPC: getFarmContext failed: {}", e.getMessage());
            throw new FarmNotFoundException(farmId.toString());
        }
    }
}
