package com.agriyield.userservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.userservice.application.port.outgoing.UserRepositoryPort;
import com.agriyield.userservice.domain.model.User;
import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto.*;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.MerchantProfileEntity;
import com.agriyield.userservice.infrastructure.repository.JpaMerchantProfileRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepositoryPort userRepository;
    private final JpaMerchantProfileRepository merchantProfileRepository;

    @Override
    public void getUserById(
            UserIdRequest request,
            StreamObserver<UserResponse> responseObserver) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            responseObserver.onNext(UserResponse.newBuilder()
                    .setId(user.getId().toString())
                    .setPhone(user.getPhone())
                    .setEmail(user.getEmail() != null ? user.getEmail() : "")
                    .setFaydaId(user.getFaydaId())
                    .setRole(user.getRole().getValue())
                    .setKycStatus(user.getKycStatus().getValue())
                    .setAccountStatus(user.getAccountStatus().getValue())
                    .setPreferredLanguage(user.getPreferredLanguage().getCode())
                    .setCreatedAt(user.getCreatedAt().toString())
                    .setUpdatedAt(user.getUpdatedAt().toString())
                    .build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid UUID").asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getFarmerProfile(
            UserIdRequest request,
            StreamObserver<FarmerProfileResponse> responseObserver) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            responseObserver.onNext(FarmerProfileResponse.newBuilder()
                    .setUserId(user.getId().toString())
                    .setAgriScore(user.getAgriScore() != null ? user.getAgriScore() : 50)
                    .setTotalSeasonsCompleted(0)
                    .setPreferredLanguage(user.getPreferredLanguage().getCode())
                    .build());
            responseObserver.onCompleted();

        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getInvestorProfile(
            UserIdRequest request,
            StreamObserver<InvestorProfileResponse> responseObserver) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            responseObserver.onNext(InvestorProfileResponse.newBuilder()
                    .setUserId(user.getId().toString())
                    .setAgriScore(user.getAgriScore() != null ? user.getAgriScore() : 50)
                    .setRiskTolerance(user.getRiskTolerance() != null
                            ? user.getRiskTolerance() : "MODERATE")
                    .setInvestmentGoal(user.getInvestmentGoal() != null
                            ? user.getInvestmentGoal() : "")
                    .setTotalInvestedEtb(0.0)
                    .setTotalReturnedEtb(0.0)
                    .setTotalSeasonsCompleted(0)
                    .build());
            responseObserver.onCompleted();

        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    // ── FIXED: now queries merchant_profiles table instead of returning ──────
    // ── hardcoded empty strings that caused merchant-service to fail    ──────
    @Override
    public void getMerchantProfile(
            UserIdRequest request,
            StreamObserver<MerchantProfileResponse> responseObserver) {
        try {
            UUID userId = UUID.fromString(request.getUserId());

            // 1. Confirm the user exists
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // 2. Load the real merchant profile row
            MerchantProfileEntity profile = merchantProfileRepository
                    .findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException(
                            "Merchant profile not found for userId: " + userId
                            + " — call POST /api/v1/merchants/register first"));

            log.info("getMerchantProfile: found profile id={} for userId={}",
                    profile.getId(), userId);

            responseObserver.onNext(MerchantProfileResponse.newBuilder()
                    .setUserId(userId.toString())
                    // Send the real merchant UUID — this is what merchant-service
                    // uses as merchantId for product ownership checks
                    .setMerchantId(profile.getId().toString())
                    .setBusinessName(
                            profile.getBusinessName() != null
                                    ? profile.getBusinessName() : "")
                    .setBusinessLicenseNumber(
                            profile.getBusinessLicenseNumber() != null
                                    ? profile.getBusinessLicenseNumber() : "")
                    .setSubscriptionTier(
                            profile.getSubscriptionTier() != null
                                    ? profile.getSubscriptionTier() : "BASIC")
                    .setIsPhysicallyVerified(
                            profile.getIsPhysicallyVerified() != null
                                    && profile.getIsPhysicallyVerified())
                    .setIsPremium(
                            profile.getIsPremium() != null
                                    && profile.getIsPremium())
                    .setKebeleCode(
                            profile.getKebeleCode() != null
                                    ? profile.getKebeleCode() : "")
                    .setTelebirrAccount(
                            profile.getTelebirrAccount() != null
                                    ? profile.getTelebirrAccount() : "")
                    .build());

            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid UUID format").asRuntimeException());
        } catch (RuntimeException e) {
            log.error("getMerchantProfile failed for userId={}: {}",
                    request.getUserId(), e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void verifyUserExists(
            PhoneRequest request,
            StreamObserver<ExistsResponse> responseObserver) {
        try {
            var userOpt = userRepository.findByPhone(request.getPhone());
            ExistsResponse.Builder builder = ExistsResponse.newBuilder()
                    .setExists(userOpt.isPresent());
            userOpt.ifPresent(u -> builder
                    .setUserId(u.getId().toString())
                    .setRole(u.getRole().getValue()));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (RuntimeException e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateAgriScore(
            UpdateAgriScoreRequest request,
            StreamObserver<EmptyResponse> responseObserver) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setAgriScore(request.getAgriScore());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            responseObserver.onNext(EmptyResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Agri-Score updated")
                    .build());
            responseObserver.onCompleted();

        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getMerchantIdsByKebele(
            KebeleRequest request,
            StreamObserver<MerchantIdsResponse> responseObserver) {
        try {
            List<String> merchantIds = merchantProfileRepository
                    .findByKebeleCode(request.getKebeleCode())
                    .stream()
                    .map(profile -> profile.getId().toString())
                    .toList();
            responseObserver.onNext(MerchantIdsResponse.newBuilder()
                    .addAllMerchantIds(merchantIds)
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
