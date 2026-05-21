package com.agriyield.farmservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.application.port.outgoing.UserServicePort;
import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserServiceGrpcClient implements UserServicePort {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Override
    public boolean verifyFarmerExists(UUID farmerId) {
        try {
            log.info("gRPC: Verifying farmer exists: {}", farmerId);
            UserIdRequest request = UserIdRequest.newBuilder()
                .setUserId(farmerId.toString())
                .build();
            UserResponse response = userServiceStub.getUserById(request);
            boolean isActive = "ACTIVE".equals(response.getAccountStatus());
            boolean isFarmer = "FARMER".equals(response.getRole());
            return isActive && isFarmer;
        } catch (Exception e) {
            log.error("gRPC: Failed to verify farmer: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void updateAgriScore(UUID farmerId, int score, UUID cropCycleId) {
        try {
            log.info("gRPC: Updating agri-score for farmer: {}, score: {}", farmerId, score);
            UpdateAgriScoreRequest request = UpdateAgriScoreRequest.newBuilder()
                .setUserId(farmerId.toString())
                .setAgriScore(score)
                .setCropCycleId(cropCycleId.toString())
                .build();
            userServiceStub.updateAgriScore(request);
        } catch (Exception e) {
            log.error("gRPC: Failed to update agri-score: {}", e.getMessage());
        }
    }
}
