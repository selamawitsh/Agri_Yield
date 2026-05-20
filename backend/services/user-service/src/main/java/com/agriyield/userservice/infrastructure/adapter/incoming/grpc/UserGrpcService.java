package com.agriyield.userservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.outgoing.UserRepositoryPort;
import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepositoryPort userRepository;

    @Override
    public void getUserById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("gRPC GetUserById: {}", request.getUserId());
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

            UserResponse response = UserResponse.newBuilder()
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
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID: " + request.getUserId())
                .asRuntimeException());
        } catch (RuntimeException e) {
            log.error("gRPC GetUserById failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getFarmerProfile(UserIdRequest request, StreamObserver<FarmerProfileResponse> responseObserver) {
        log.info("gRPC GetFarmerProfile: {}", request.getUserId());
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

            if (!"FARMER".equals(user.getRole().getValue())) {
                responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("User is not a FARMER")
                    .asRuntimeException());
                return;
            }

            FarmerProfileResponse response = FarmerProfileResponse.newBuilder()
                .setUserId(user.getId().toString())
                .setAgriScore(user.getAgriScore() != null ? user.getAgriScore() : 50)
                .setTotalSeasonsCompleted(0)
                .setPreferredLanguage(user.getPreferredLanguage().getCode())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID: " + request.getUserId())
                .asRuntimeException());
        } catch (RuntimeException e) {
            log.error("gRPC GetFarmerProfile failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getInvestorProfile(UserIdRequest request, StreamObserver<InvestorProfileResponse> responseObserver) {
        log.info("gRPC GetInvestorProfile: {}", request.getUserId());
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

            if (!"INVESTOR".equals(user.getRole().getValue())) {
                responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("User is not an INVESTOR")
                    .asRuntimeException());
                return;
            }

            InvestorProfileResponse response = InvestorProfileResponse.newBuilder()
                .setUserId(user.getId().toString())
                .setAgriScore(user.getAgriScore() != null ? user.getAgriScore() : 50)
                .setRiskTolerance(user.getRiskTolerance() != null ? user.getRiskTolerance() : "MODERATE")
                .setInvestmentGoal(user.getInvestmentGoal() != null ? user.getInvestmentGoal() : "")
                .setTotalInvestedEtb(0.0)
                .setTotalReturnedEtb(0.0)
                .setTotalSeasonsCompleted(0)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID: " + request.getUserId())
                .asRuntimeException());
        } catch (RuntimeException e) {
            log.error("gRPC GetInvestorProfile failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void getMerchantProfile(UserIdRequest request, StreamObserver<MerchantProfileResponse> responseObserver) {
        log.info("gRPC GetMerchantProfile: {}", request.getUserId());
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

            if (!"MERCHANT".equals(user.getRole().getValue())) {
                responseObserver.onError(Status.FAILED_PRECONDITION
                    .withDescription("User is not a MERCHANT")
                    .asRuntimeException());
                return;
            }

            // Merchant-specific fields are in MerchantProfileEntity, not on User
            // We return what we have on the User domain model here
            MerchantProfileResponse response = MerchantProfileResponse.newBuilder()
                .setUserId(user.getId().toString())
                .setBusinessName("")
                .setBusinessLicenseNumber("")
                .setSubscriptionTier("BASIC")
                .setIsPhysicallyVerified(false)
                .setIsPremium(false)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID: " + request.getUserId())
                .asRuntimeException());
        } catch (RuntimeException e) {
            log.error("gRPC GetMerchantProfile failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void verifyUserExists(PhoneRequest request, StreamObserver<ExistsResponse> responseObserver) {
        log.info("gRPC VerifyUserExists: {}", request.getPhone());
        try {
            boolean exists = userRepository.existsByPhone(request.getPhone());

            ExistsResponse.Builder builder = ExistsResponse.newBuilder().setExists(exists);

            if (exists) {
                userRepository.findByPhone(request.getPhone()).ifPresent(user -> {
                    builder.setUserId(user.getId().toString());
                    builder.setRole(user.getRole().getValue());
                });
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (RuntimeException e) {
            log.error("gRPC VerifyUserExists failed: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void updateAgriScore(UpdateAgriScoreRequest request, StreamObserver<EmptyResponse> responseObserver) {
        log.info("gRPC UpdateAgriScore: userId={}, score={}", request.getUserId(), request.getAgriScore());
        try {
            UUID userId = UUID.fromString(request.getUserId());
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getUserId()));

            user.setAgriScore(request.getAgriScore());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            responseObserver.onNext(EmptyResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Agri-Score updated successfully")
                .build());
            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID: " + request.getUserId())
                .asRuntimeException());
        } catch (RuntimeException e) {
            log.error("gRPC UpdateAgriScore failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }
}
