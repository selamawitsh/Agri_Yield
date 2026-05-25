package com.agriyield.merchantservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.merchantservice.application.port.outgoing.UserServicePort;
import com.agriyield.merchantservice.domain.enums.SubscriptionTier;
import com.agriyield.merchantservice.domain.model.MerchantProfile;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Calls user-service gRPC to fetch merchant profile data.
 *
 * NOTE: Replace the stub body with real generated proto calls
 * once your shared proto files compile. The method signatures
 * and domain mapping below are correct and complete.
 */
@Slf4j
@Component
public class UserServiceGrpcClient implements UserServicePort {

    // @GrpcClient("user-service")
    // private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Override
    public Optional<MerchantProfile> getMerchantProfileByUserId(UUID userId) {
        log.info("Calling user-service gRPC GetMerchantProfile for userId={}", userId);
        try {
            /*
             * When proto is ready, replace this block with:
             *
             * MerchantProfileResponse response = userServiceStub.getMerchantProfile(
             *     UserIdRequest.newBuilder().setUserId(userId.toString()).build()
             * );
             * return Optional.of(mapToDomain(response));
             */

            // Temporary stub — returns empty until proto is wired
            return Optional.empty();

        } catch (Exception e) {
            log.error("Failed to fetch merchant profile from user-service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<MerchantProfile> getMerchantProfileById(UUID merchantId) {
        log.info("Calling user-service gRPC GetMerchantProfile for merchantId={}", merchantId);
        try {
            // Same pattern as above — wire proto when ready
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to fetch merchant by id from user-service: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean merchantExists(UUID merchantId) {
        return getMerchantProfileById(merchantId).isPresent();
    }

    /*
     * When proto is ready, use this mapper:
     *
     * private MerchantProfile mapToDomain(MerchantProfileResponse r) {
     *     return MerchantProfile.builder()
     *         .id(UUID.fromString(r.getId()))
     *         .userId(UUID.fromString(r.getUserId()))
     *         .businessName(r.getBusinessName())
     *         .businessLicenseNumber(r.getBusinessLicenseNumber())
     *         .storeGpsLat(r.getStoreGpsLat())
     *         .storeGpsLng(r.getStoreGpsLng())
     *         .isPhysicallyVerified(r.getIsPhysicallyVerified())
     *         .telebirrAccount(r.getTelebirrAccount())
     *         .subscriptionTier(SubscriptionTier.valueOf(r.getSubscriptionTier()))
     *         .build();
     * }
     */
}
