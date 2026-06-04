package com.agriyield.weatherservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.grpc.FarmServiceGrpc;
import com.agriyield.farmservice.grpc.FarmServiceProto.FarmIdRequest;
import com.agriyield.farmservice.grpc.FarmServiceProto.FarmResponse;
import com.agriyield.weatherservice.application.port.outgoing.FarmServiceClientPort;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FarmServiceGrpcClient implements FarmServiceClientPort {

    @GrpcClient("farm-service")
    private FarmServiceGrpc.FarmServiceBlockingStub farmServiceStub;

    @Override
    public List<UUID> getActiveFarmIds() {

        log.info("getActiveFarmIds not implemented yet");

        return new ArrayList<>();
    }

    @Override
    public double[] getFarmCoordinates(UUID farmId) {

        log.info("Fetching coordinates from farm-service for {}", farmId);

        FarmIdRequest request =
                FarmIdRequest.newBuilder()
                        .setFarmId(farmId.toString())
                        .build();

        FarmResponse response =
                farmServiceStub.getFarmById(request);

        return new double[]{
                response.getGpsCentroidLat(),
                response.getGpsCentroidLng()
        };
    }
}