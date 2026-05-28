package com.agriyield.weatherservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.weatherservice.application.port.outgoing.FarmServiceClientPort;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * gRPC client for farm-service.
 * Wire the generated stub when proto is compiled.
 */
@Slf4j
@Component
public class FarmServiceGrpcClient implements FarmServiceClientPort {

    // @GrpcClient("farm-service")
    // private FarmServiceGrpc.FarmServiceBlockingStub farmServiceStub;

    @Override
    public List<UUID> getActiveFarmIds() {
        log.info("gRPC: Getting active farm IDs from farm-service");
        // When proto ready:
        // return stub.getAllActiveFarms(...) mapped to UUIDs
        return new ArrayList<>();
    }

    @Override
    public double[] getFarmCoordinates(UUID farmId) {
        log.info("gRPC: Getting farm coordinates for farmId={}", farmId);
        // When proto ready:
        // FarmResponse r = farmServiceStub.getFarmById(...)
        // return new double[]{r.getGpsCentroidLat(), r.getGpsCentroidLng()};
        return new double[]{9.0, 38.7}; // Addis Ababa fallback
    }
}
