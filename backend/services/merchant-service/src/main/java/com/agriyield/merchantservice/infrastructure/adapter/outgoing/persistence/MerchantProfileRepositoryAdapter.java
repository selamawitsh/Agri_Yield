package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.merchantservice.application.port.outgoing.MerchantProfileRepositoryPort;
import com.agriyield.merchantservice.application.port.outgoing.UserServicePort;
import com.agriyield.merchantservice.domain.model.MerchantProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Delegates all merchant profile reads to user-service via gRPC.
 * No local DB table for merchant_profiles in merchant-service.
 */
@Component
@RequiredArgsConstructor
public class MerchantProfileRepositoryAdapter implements MerchantProfileRepositoryPort {

    private final UserServicePort userServicePort;

    @Override
    public Optional<MerchantProfile> findByUserId(UUID userId) {
        return userServicePort.getMerchantProfileByUserId(userId);
    }

    @Override
    public Optional<MerchantProfile> findById(UUID id) {
        return userServicePort.getMerchantProfileById(id);
    }

    @Override
    public List<MerchantProfile> findAll() {
        // When proto is wired, call userServiceStub.listMerchants()
        // For now returns empty — nightly job will skip until wired
        return List.of();
    }
}
