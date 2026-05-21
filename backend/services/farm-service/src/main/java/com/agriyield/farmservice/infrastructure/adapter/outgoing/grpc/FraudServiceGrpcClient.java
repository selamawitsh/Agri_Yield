package com.agriyield.farmservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.farmservice.application.port.outgoing.FraudServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

// Stub implementation — fraud-service gRPC will be wired when fraud-service is built
@Slf4j
@Component
public class FraudServiceGrpcClient implements FraudServicePort {

    @Override
    public GpsVerificationResult verifyGpsConsistency(UUID farmId,
                                                       BigDecimal photoLat,
                                                       BigDecimal photoLng,
                                                       String farmGeoJsonPolygon) {
        log.info("STUB: GPS verification for farm: {} — returning consistent=true", farmId);
        // Stub always returns consistent until fraud-service is built
        return new GpsVerificationResult(true, 0.0);
    }
}
