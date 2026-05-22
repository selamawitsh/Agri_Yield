package com.agriyield.investmentservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.investmentservice.application.port.outgoing.UserServicePort;
import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto;
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
    public boolean verifyInvestorExists(UUID investorId) {
        try {
            log.info("gRPC: verifyInvestorExists: {}", investorId);
            UserServiceProto.UserResponse response = userStub.getUserById(
                UserServiceProto.UserIdRequest.newBuilder()
                    .setUserId(investorId.toString())
                    .build());
            return "ACTIVE".equals(response.getAccountStatus())
                && "INVESTOR".equals(response.getRole());
        } catch (Exception e) {
            log.error("gRPC: verifyInvestorExists failed: {}", e.getMessage());
            return false;
        }
    }
}
