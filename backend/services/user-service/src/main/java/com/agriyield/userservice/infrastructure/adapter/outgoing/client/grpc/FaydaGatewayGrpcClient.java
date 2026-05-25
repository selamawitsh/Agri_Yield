package com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc;

import com.agriyield.userservice.application.port.outgoing.FaydaVerificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FaydaGatewayGrpcClient implements FaydaVerificationPort {

    @Value("${app.fayda.mock-enabled:true}")
    private boolean mockEnabled;

    @Override
    public boolean verifyFaydaId(String faydaId, String phone,
                                  String fullName) {
        if (mockEnabled) {
            log.info("MOCK Fayda verify — faydaId: {}", faydaId);
            return true;
        }
        // TODO: real gRPC call to fayda-gateway:9082
        return true;
    }

    @Override
    public KycData pullKycData(String faydaId) {
        if (mockEnabled) {
            log.info("MOCK Fayda KYC pull — faydaId: {}", faydaId);
            return new KycData("Mock User", "1990-01-01", "Oromia");
        }
        // TODO: real gRPC call to fayda-gateway:9082
        return new KycData("User", "1990-01-01", "Oromia");
    }
}
