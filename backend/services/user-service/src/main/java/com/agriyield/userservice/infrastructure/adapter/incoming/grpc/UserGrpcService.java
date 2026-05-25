package com.agriyield.userservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.userservice.application.port.outgoing.UserRepositoryPort;
import com.agriyield.userservice.domain.model.User;
import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto.*;
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
public class UserGrpcService
        extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepositoryPort userRepository;

    private final JpaMerchantProfileRepository
            merchantProfileRepository;

    @Override
    public void getUserById(
            UserIdRequest request,
            StreamObserver<UserResponse> responseObserver
    ) {

        try {

            UUID userId =
                    UUID.fromString(request.getUserId());

            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found: " + userId
                                    ));

            responseObserver.onNext(
                    UserResponse.newBuilder()
                            .setId(user.getId().toString())
                            .setPhone(user.getPhone())
                            .setEmail(
                                    user.getEmail() != null
                                            ? user.getEmail()
                                            : ""
                            )
                            .setFaydaId(user.getFaydaId())
                            .setRole(user.getRole().getValue())
                            .setKycStatus(
                                    user.getKycStatus().getValue()
                            )
                            .setAccountStatus(
                                    user.getAccountStatus().getValue()
                            )
                            .setPreferredLanguage(
                                    user.getPreferredLanguage().getCode()
                            )
                            .setCreatedAt(
                                    user.getCreatedAt().toString()
                            )
                            .setUpdatedAt(
                                    user.getUpdatedAt().toString()
                            )
                            .build()
            );

            responseObserver.onCompleted();

        } catch (IllegalArgumentException e) {

            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid UUID")
                            .asRuntimeException()
            );

        } catch (RuntimeException e) {

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getFarmerProfile(
            UserIdRequest request,
            StreamObserver<FarmerProfileResponse>
                    responseObserver
    ) {

        try {

            UUID userId =
                    UUID.fromString(request.getUserId());

            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found"
                                    ));

            responseObserver.onNext(
                    FarmerProfileResponse.newBuilder()
                            .setUserId(user.getId().toString())
                            .setAgriScore(
                                    user.getAgriScore() != null
                                            ? user.getAgriScore()
                                            : 50
                            )
                            .setTotalSeasonsCompleted(0)
                            .setPreferredLanguage(
                                    user.getPreferredLanguage().getCode()
                            )
                            .build()
            );

            responseObserver.onCompleted();

        } catch (RuntimeException e) {

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getInvestorProfile(
            UserIdRequest request,
            StreamObserver<InvestorProfileResponse>
                    responseObserver
    ) {

        try {

            UUID userId =
                    UUID.fromString(request.getUserId());

            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found"
                                    ));

            responseObserver.onNext(
                    InvestorProfileResponse.newBuilder()
                            .setUserId(user.getId().toString())
                            .setAgriScore(
                                    user.getAgriScore() != null
                                            ? user.getAgriScore()
                                            : 50
                            )
                            .setRiskTolerance(
                                    user.getRiskTolerance() != null
                                            ? user.getRiskTolerance()
                                            : "MODERATE"
                            )
                            .setInvestmentGoal(
                                    user.getInvestmentGoal() != null
                                            ? user.getInvestmentGoal()
                                            : ""
                            )
                            .setTotalInvestedEtb(0.0)
                            .setTotalReturnedEtb(0.0)
                            .setTotalSeasonsCompleted(0)
                            .build()
            );

            responseObserver.onCompleted();

        } catch (RuntimeException e) {

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getMerchantProfile(
            UserIdRequest request,
            StreamObserver<MerchantProfileResponse>
                    responseObserver
    ) {

        try {

            UUID userId =
                    UUID.fromString(request.getUserId());

            userRepository.findById(userId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "User not found"
                            ));

            responseObserver.onNext(
                    MerchantProfileResponse.newBuilder()
                            .setUserId(userId.toString())
                            .setBusinessName("")
                            .setBusinessLicenseNumber("")
                            .setSubscriptionTier("BASIC")
                            .setIsPhysicallyVerified(false)
                            .setIsPremium(false)
                            .build()
            );

            responseObserver.onCompleted();

        } catch (RuntimeException e) {

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void verifyUserExists(
            PhoneRequest request,
            StreamObserver<ExistsResponse>
                    responseObserver
    ) {

        try {

            var userOpt =
                    userRepository.findByPhone(
                            request.getPhone()
                    );

            ExistsResponse.Builder builder =
                    ExistsResponse.newBuilder()
                            .setExists(userOpt.isPresent());

            userOpt.ifPresent(u ->
                    builder
                            .setUserId(u.getId().toString())
                            .setRole(u.getRole().getValue())
            );

            responseObserver.onNext(builder.build());

            responseObserver.onCompleted();

        } catch (RuntimeException e) {

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void updateAgriScore(
            UpdateAgriScoreRequest request,
            StreamObserver<EmptyResponse>
                    responseObserver
    ) {

        try {

            UUID userId =
                    UUID.fromString(request.getUserId());

            User user =
                    userRepository.findById(userId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "User not found"
                                    ));

            user.setAgriScore(request.getAgriScore());

            user.setUpdatedAt(LocalDateTime.now());

            userRepository.save(user);

            responseObserver.onNext(
                    EmptyResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Agri-Score updated")
                            .build()
            );

            responseObserver.onCompleted();

        } catch (RuntimeException e) {

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getMerchantIdsByKebele(
            KebeleRequest request,
            StreamObserver<MerchantIdsResponse>
                    responseObserver
    ) {

        try {

            List<String> merchantIds =
                    merchantProfileRepository
                            .findByKebeleCode(
                                    request.getKebeleCode()
                            )
                            .stream()
                            .map(profile ->
                                    profile.getId().toString()
                            )
                            .toList();

            MerchantIdsResponse response =
                    MerchantIdsResponse.newBuilder()
                            .addAllMerchantIds(merchantIds)
                            .build();

            responseObserver.onNext(response);

            responseObserver.onCompleted();

        } catch (Exception e) {

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}