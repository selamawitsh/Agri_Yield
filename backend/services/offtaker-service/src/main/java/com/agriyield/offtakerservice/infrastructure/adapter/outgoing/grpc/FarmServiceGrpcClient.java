package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.grpc.FarmServiceGrpc;
import com.agriyield.farmservice.grpc.FarmServiceProto;
import com.agriyield.offtakerservice.application.port.outgoing.FarmServicePort;
import com.agriyield.offtakerservice.domain.exception.ResourceNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FarmServiceGrpcClient implements FarmServicePort {

    @GrpcClient("farm-service")
    private FarmServiceGrpc.FarmServiceBlockingStub farmStub;

    @Override
    public Map<String, Object> getFarmById(String farmId) {
        try {
            FarmServiceProto.FarmResponse response = farmStub.getFarmById(
                FarmServiceProto.FarmIdRequest.newBuilder()
                    .setFarmId(farmId)
                    .build());

            Map<String, Object> farm = new HashMap<>();
            farm.put("farmId",            response.getFarmId());
            farm.put("farmerId",          response.getFarmerId());
            farm.put("cropType",          response.getCropType());
            farm.put("areaHectares",      response.getAreaHectares());
            farm.put("status",            response.getStatus());
            farm.put("region",            response.getRegion());
            farm.put("kebeleCode",        response.getKebeleCode());
            farm.put("gpsCentroidLat",    response.getGpsCentroidLat());
            farm.put("gpsCentroidLng",    response.getGpsCentroidLng());
            farm.put("satelliteVerified", response.getSatelliteVerified());
            return farm;

        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResourceNotFoundException("Farm not found: " + farmId);
            }
            log.error("FarmService.getFarmById failed: {}", e.getMessage());
            throw new RuntimeException("Farm service error: " + e.getMessage());
        }
    }
}
