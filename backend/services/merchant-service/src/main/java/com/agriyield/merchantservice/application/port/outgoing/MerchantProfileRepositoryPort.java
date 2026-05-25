package com.agriyield.merchantservice.application.port.outgoing;

import com.agriyield.merchantservice.domain.model.MerchantProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Merchant profile data comes from user-service via gRPC.
 * This port is implemented by UserServiceGrpcClient.
 * Merchant-service NEVER writes merchant profiles — that is user-service's job.
 */
public interface MerchantProfileRepositoryPort {
    Optional<MerchantProfile> findByUserId(UUID userId);
    Optional<MerchantProfile> findById(UUID id);
    // findAll needed only for nightly price job — fetches all merchants from user-service
    List<MerchantProfile> findAll();
}
