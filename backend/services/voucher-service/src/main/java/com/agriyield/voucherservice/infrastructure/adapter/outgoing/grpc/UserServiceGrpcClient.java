package com.agriyield.voucherservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto;
import com.agriyield.voucherservice.application.port.outgoing.UserServicePort;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserServiceGrpcClient implements UserServicePort {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    @Override
    public boolean verifyMerchantExists(UUID merchantId) {
        try {
            log.info("gRPC: verifyMerchantExists: {}", merchantId);
            UserServiceProto.UserResponse response = userStub.getUserById(
                UserServiceProto.UserIdRequest.newBuilder()
                    .setUserId(merchantId.toString())
                    .build());
            return "ACTIVE".equals(response.getAccountStatus())
                && "MERCHANT".equals(response.getRole());
        } catch (Exception e) {
            log.error("gRPC: verifyMerchantExists failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean verifyFarmerExists(UUID farmerId) {
        try {
            log.info("gRPC: verifyFarmerExists: {}", farmerId);
            UserServiceProto.UserResponse response = userStub.getUserById(
                UserServiceProto.UserIdRequest.newBuilder()
                    .setUserId(farmerId.toString())
                    .build());
            return "ACTIVE".equals(response.getAccountStatus())
                && "FARMER".equals(response.getRole());
        } catch (Exception e) {
            log.error("gRPC: verifyFarmerExists failed: {}", e.getMessage());
            return false;
        }
    }
}
