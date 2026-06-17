package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.offtakerservice.application.port.outgoing.UserServicePort;
import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserServiceGrpcClient implements UserServicePort {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    @Override
    public Map<String, Object> getUserById(String userId) {
        try {
            UserServiceProto.UserResponse response = userStub.getUserById(
                    UserServiceProto.UserIdRequest.newBuilder()
                            .setUserId(userId)
                            .build());

            Map<String, Object> user = new HashMap<>();
            user.put("id", response.getId());
            user.put("phone", response.getPhone());
            user.put("email", response.getEmail());
            user.put("faydaId", response.getFaydaId());
            user.put("role", response.getRole());
            user.put("kycStatus", response.getKycStatus());
            user.put("accountStatus", response.getAccountStatus());
            return user;

        } catch (StatusRuntimeException e) {
            log.warn("UserService.getUserById failed for userId={}: {}", userId, e.getMessage());
            return Map.of();
        }
    }

    @Override
    public Map<String, Object> getFarmerProfile(String userId) {
        try {
            UserServiceProto.FarmerProfileResponse response = userStub.getFarmerProfile(
                    UserServiceProto.UserIdRequest.newBuilder()
                            .setUserId(userId)
                            .build());

            Map<String, Object> profile = new HashMap<>();
            profile.put("userId", response.getUserId());
            profile.put("agriScore", response.getAgriScore());
            profile.put("totalSeasonsCompleted", response.getTotalSeasonsCompleted());
            profile.put("preferredLanguage", response.getPreferredLanguage());
            return profile;

        } catch (StatusRuntimeException e) {
            log.warn("UserService.getFarmerProfile failed for userId={}: {}", userId, e.getMessage());
            return Map.of();
        }
    }
}
