package com.agriyield.fraudservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.fraudservice.application.port.incoming.FraudServicePort;
import com.agriyield.fraudservice.grpc.DuplicateRedemptionResponse;
import com.agriyield.fraudservice.grpc.GpsAnomalyResponse;
import com.agriyield.fraudservice.grpc.GpsRequest;
import com.agriyield.fraudservice.grpc.ImageMetadataRequest;
import com.agriyield.fraudservice.grpc.MerchantEligibilityRequest;
import com.agriyield.fraudservice.grpc.MerchantEligibilityResponse;
import com.agriyield.fraudservice.grpc.QrSignatureRequest;
import com.agriyield.fraudservice.grpc.ValidationResponse;
import com.agriyield.fraudservice.grpc.VoucherRedemptionRequest;
import com.agriyield.fraudservice.grpc.FraudServiceGrpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class FraudGrpcService extends FraudServiceGrpc.FraudServiceImplBase {

    private final FraudServicePort fraudService;

    /** FR-02: ValidateImageMetadata */
    @Override
    public void validateImageMetadata(
            ImageMetadataRequest request,
            StreamObserver<ValidationResponse> responseObserver) {

        log.info("gRPC FR-02: validateImageMetadata farm={}", request.getFarmId());

        try {

            FraudServicePort.ExifValidationResult result =
                    fraudService.validateImageMetadata(
                            UUID.fromString(request.getFarmId()),
                            UUID.fromString(request.getEntityId()),
                            request.getPhotoLat(),
                            request.getPhotoLng(),
                            request.getPhotoTimestamp()
                    );

            ValidationResponse response = ValidationResponse.newBuilder()
                    .setValid(result.valid())
                    .setFailureReason(
                            result.failureReason() != null
                                    ? result.failureReason()
                                    : ""
                    )
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC validateImageMetadata failed", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    /** FR-03: ValidateVoucherRedemption */
    @Override
    public void validateVoucherRedemption(
            VoucherRedemptionRequest request,
            StreamObserver<DuplicateRedemptionResponse> responseObserver) {

        log.info("gRPC FR-03: validateVoucherRedemption code={}", request.getVoucherCode());

        try {

            FraudServicePort.DuplicateRedemptionResult result =
                    fraudService.validateVoucherRedemption(
                            request.getVoucherCode(),
                            UUID.fromString(request.getMerchantId())
                    );

            DuplicateRedemptionResponse response =
                    DuplicateRedemptionResponse.newBuilder()
                            .setIsDuplicate(result.isDuplicate())
                            .setFirstScanTimestamp(
                                    result.firstScanTimestamp() != null
                                            ? result.firstScanTimestamp()
                                            : ""
                            )
                            .setFailureReason(
                                    result.failureReason() != null
                                            ? result.failureReason()
                                            : ""
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC validateVoucherRedemption failed", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    /** FR-04: ValidateQrSignature */
    @Override
    public void validateQrSignature(
            QrSignatureRequest request,
            StreamObserver<ValidationResponse> responseObserver) {

        log.info("gRPC FR-04: validateQrSignature code={}", request.getVoucherCode());

        try {

            FraudServicePort.QrSignatureResult result =
                    fraudService.validateQrSignature(
                            request.getVoucherCode(),
                            request.getQrPayload(),
                            request.getSignature()
                    );

            ValidationResponse response = ValidationResponse.newBuilder()
                    .setValid(result.valid())
                    .setFailureReason(
                            result.failureReason() != null
                                    ? result.failureReason()
                                    : ""
                    )
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC validateQrSignature failed", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    /** FR-05: DetectSuspiciousGps */
    @Override
    public void detectSuspiciousGps(
            GpsRequest request,
            StreamObserver<GpsAnomalyResponse> responseObserver) {

        log.info("gRPC FR-05: detectSuspiciousGps entity={}", request.getEntityId());

        try {

            FraudServicePort.GpsAnomalyResult result =
                    fraudService.detectSuspiciousGps(
                            UUID.fromString(request.getEntityId()),
                            request.getEntityType(),
                            request.getLatitude(),
                            request.getLongitude(),
                            request.getContext()
                    );

            GpsAnomalyResponse response =
                    GpsAnomalyResponse.newBuilder()
                            .setSuspicious(result.suspicious())
                            .setDistanceKm(result.distanceKm())
                            .setTimeDeltaMinutes(result.timeDeltaMinutes())
                            .setFailureReason(
                                    result.failureReason() != null
                                            ? result.failureReason()
                                            : ""
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC detectSuspiciousGps failed", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    /** FR-10: ValidateMerchantEligibility */
    @Override
    public void validateMerchantEligibility(
            MerchantEligibilityRequest request,
            StreamObserver<MerchantEligibilityResponse> responseObserver) {

        log.info(
                "gRPC FR-10: validateMerchantEligibility merchant={}",
                request.getMerchantId()
        );

        try {

            FraudServicePort.MerchantEligibilityResult result =
                    fraudService.validateMerchantEligibility(
                            UUID.fromString(request.getMerchantId())
                    );

            MerchantEligibilityResponse response =
                    MerchantEligibilityResponse.newBuilder()
                            .setEligible(result.eligible())
                            .setFailureReason(
                                    result.failureReason() != null
                                            ? result.failureReason()
                                            : ""
                            )
                            .setFraudScore(result.fraudScore())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC validateMerchantEligibility failed", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}