package com.agriyield.faydagateway.application.service;

import com.agriyield.faydagateway.application.port.incoming.FaydaServicePort;
import com.agriyield.faydagateway.application.port.outgoing.CachePort;
import com.agriyield.faydagateway.application.port.outgoing.FaydaApiPort;
import com.agriyield.faydagateway.domain.model.IdentityVerificationResult;
import com.agriyield.faydagateway.domain.model.KycData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaydaService implements FaydaServicePort {

    private final FaydaApiPort faydaApiPort;
    private final CachePort cachePort;
    private final ObjectMapper objectMapper;

    // Per SRS: identity TTL=24h, KYC TTL=7d
    @Value("${app.redis.identity-ttl-hours:24}")
    private int identityTtlHours;

    @Value("${app.redis.kyc-ttl-days:7}")
    private int kycTtlDays;

    private static final String IDENTITY_CACHE_PREFIX = "fayda:identity:";
    private static final String KYC_CACHE_PREFIX = "fayda:kyc:";

    @Override
    public IdentityVerificationResult verifyIdentity(String faydaId, String phone, String fullName) {
        String cacheKey = IDENTITY_CACHE_PREFIX + faydaId;

        // Check Redis cache first (TTL 24h per SRS)
        Optional<String> cached = cachePort.get(cacheKey);
        if (cached.isPresent()) {
            log.info("Cache HIT for identity verification: faydaId={}", faydaId);
            try {
                return objectMapper.readValue(cached.get(), IdentityVerificationResult.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize cached identity result for faydaId={}", faydaId);
            }
        }

        log.info("Cache MISS — calling Fayda API for faydaId={}", faydaId);
        IdentityVerificationResult result = faydaApiPort.verifyIdentity(faydaId, phone, fullName);

        // Cache only successful verifications
        if (result.isVerified()) {
            try {
                String serialized = objectMapper.writeValueAsString(result);
                cachePort.set(cacheKey, serialized, Duration.ofHours(identityTtlHours));
                log.info("Cached identity verification for faydaId={}, TTL={}h", faydaId, identityTtlHours);
            } catch (Exception e) {
                log.warn("Failed to cache identity result for faydaId={}", faydaId);
            }
        }

        return result;
    }

    @Override
    public KycData pullKycData(String faydaId) {
        String cacheKey = KYC_CACHE_PREFIX + faydaId;

        // Check Redis cache first (TTL 7d per SRS)
        Optional<String> cached = cachePort.get(cacheKey);
        if (cached.isPresent()) {
            log.info("Cache HIT for KYC data: faydaId={}", faydaId);
            try {
                return objectMapper.readValue(cached.get(), KycData.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize cached KYC data for faydaId={}", faydaId);
            }
        }

        log.info("Cache MISS — pulling KYC data from Fayda API for faydaId={}", faydaId);
        KycData kycData = faydaApiPort.pullKycData(faydaId);

        if (kycData != null && kycData.isVerified()) {
            try {
                String serialized = objectMapper.writeValueAsString(kycData);
                cachePort.set(cacheKey, serialized, Duration.ofDays(kycTtlDays));
                log.info("Cached KYC data for faydaId={}, TTL={}d", faydaId, kycTtlDays);
            } catch (Exception e) {
                log.warn("Failed to cache KYC data for faydaId={}", faydaId);
            }
        }

        return kycData;
    }
}
