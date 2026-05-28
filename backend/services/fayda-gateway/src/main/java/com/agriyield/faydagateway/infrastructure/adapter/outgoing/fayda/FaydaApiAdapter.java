package com.agriyield.faydagateway.infrastructure.adapter.outgoing.fayda;

import com.agriyield.faydagateway.application.port.outgoing.FaydaApiPort;
import com.agriyield.faydagateway.domain.model.IdentityVerificationResult;
import com.agriyield.faydagateway.domain.model.KycData;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FaydaApiAdapter implements FaydaApiPort {

    @Value("${app.fayda.mock-enabled:true}")
    private boolean mockEnabled;

    @Value("${app.fayda.api-base-url:https://api.fayda.et/v1}")
    private String apiBaseUrl;

    @Value("${app.fayda.api-key:mock-key}")
    private String apiKey;

    @Override
    @CircuitBreaker(name = "fayda-api", fallbackMethod = "verifyIdentityFallback")
    public IdentityVerificationResult verifyIdentity(String faydaId, String phone, String fullName) {
        if (mockEnabled) {
            return mockVerifyIdentity(faydaId, phone, fullName);
        }
        // TODO: Real Fayda API integration when credentials are available
        // POST ${apiBaseUrl}/identity/verify with Bearer ${apiKey}
        log.warn("Real Fayda API not yet integrated — using mock for faydaId={}", faydaId);
        return mockVerifyIdentity(faydaId, phone, fullName);
    }

    @Override
    @CircuitBreaker(name = "fayda-api", fallbackMethod = "pullKycDataFallback")
    public KycData pullKycData(String faydaId) {
        if (mockEnabled) {
            return mockPullKycData(faydaId);
        }
        // TODO: Real Fayda API integration when credentials are available
        log.warn("Real Fayda API not yet integrated — using mock for faydaId={}", faydaId);
        return mockPullKycData(faydaId);
    }

    // ── Mock implementations ──────────────────────────────────────────────────

    private IdentityVerificationResult mockVerifyIdentity(String faydaId, String phone, String fullName) {
        log.info("[MOCK] Fayda identity verify — faydaId={}, phone={}, name={}", faydaId, phone, fullName);
        // In mock mode all valid-format Fayda IDs pass
        boolean isValid = faydaId != null && faydaId.length() >= 8;
        return IdentityVerificationResult.builder()
                .verified(isValid)
                .faydaId(faydaId)
                .message(isValid ? "Identity verified successfully (mock)" : "Invalid Fayda ID format")
                .build();
    }

    private KycData mockPullKycData(String faydaId) {
        log.info("[MOCK] Fayda KYC pull — faydaId={}", faydaId);
        return KycData.builder()
                .faydaId(faydaId)
                .fullName("Mock User — " + faydaId)
                .dateOfBirth("1990-01-01")
                .region("Oromia")
                .verified(true)
                .build();
    }

    // ── Circuit breaker fallbacks ─────────────────────────────────────────────

    public IdentityVerificationResult verifyIdentityFallback(String faydaId, String phone,
                                                              String fullName, Exception ex) {
        log.error("Fayda API circuit breaker OPEN for verifyIdentity — faydaId={}, error={}",
                faydaId, ex.getMessage());
        return IdentityVerificationResult.builder()
                .verified(false)
                .faydaId(faydaId)
                .message("Fayda service temporarily unavailable. Please try again later.")
                .build();
    }

    public KycData pullKycDataFallback(String faydaId, Exception ex) {
        log.error("Fayda API circuit breaker OPEN for pullKycData — faydaId={}, error={}",
                faydaId, ex.getMessage());
        return KycData.builder()
                .faydaId(faydaId)
                .verified(false)
                .fullName("Unavailable")
                .build();
    }
}
