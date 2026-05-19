package com.agriyield.userservice.infrastructure.adapter.outgoing.client.grpc;

import com.agriyield.userservice.core.port.outgoing.FaydaVerificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FaydaGatewayGrpcClient implements FaydaVerificationPort {
    
    @Value("${app.fayda.mock-enabled:true}")
    private boolean mockEnabled;
    
    @Override
    public boolean verifyFaydaId(String faydaId, String phone, String fullName) {
        log.info("Verifying Fayda ID: {} for phone: {} (mock: {})", faydaId, phone, mockEnabled);
        return true; // Mock implementation always returns true
    }
    
    @Override
    public KycData pullKycData(String faydaId) {
        log.info("Pulling KYC data for Fayda ID: {} (mock: {})", faydaId, mockEnabled);
        return new KycData(
            faydaId,
            "Test Farmer",
            "1990-01-01",
            "Addis Ababa",
            true
        );
    }
}
