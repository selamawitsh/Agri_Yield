package com.agriyield.voucherservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.grpc.FarmServiceGrpc;
import com.agriyield.farmservice.grpc.FarmServiceProto;
import com.agriyield.voucherservice.application.port.outgoing.FarmServicePort;
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
    public FarmContext getFarmContext(UUID farmId) {
        log.info("gRPC: getFarmContext farmId={}", farmId);
        try {
            FarmServiceProto.FarmContextResponse response = farmStub.getFarmContext(
                    FarmServiceProto.FarmIdRequest.newBuilder()
                            .setFarmId(farmId.toString())
                            .build());

            return new FarmContext(
                    response.getFarmId(),
                    response.getFarmerId(),
                    response.getCropType(),
                    response.getRegion(),
                    response.getCropCycleId(),
                    response.getSeasonName(),
                    response.getCropCycleStatus()
            );
        } catch (Exception e) {
            log.error("gRPC: getFarmContext failed: {}", e.getMessage());
            throw new RuntimeException("Could not retrieve farm context: " + e.getMessage());
        }
    }
}