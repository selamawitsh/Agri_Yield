package com.agriyield.faydagateway.infrastructure.adapter.incoming.grpc;

import com.agriyield.fayda.grpc.FaydaServiceGrpc;
import com.agriyield.fayda.grpc.PullKycDataRequest;
import com.agriyield.fayda.grpc.PullKycDataResponse;
import com.agriyield.fayda.grpc.VerifyIdentityRequest;
import com.agriyield.fayda.grpc.VerifyIdentityResponse;

import com.agriyield.faydagateway.application.port.incoming.FaydaServicePort;
import com.agriyield.faydagateway.domain.model.IdentityVerificationResult;
import com.agriyield.faydagateway.domain.model.KycData;

import io.grpc.stub.StreamObserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class FaydaGrpcService extends FaydaServiceGrpc.FaydaServiceImplBase {

    private final FaydaServicePort faydaServicePort;

    @Override
    public void verifyIdentity(
            VerifyIdentityRequest request,
            StreamObserver<VerifyIdentityResponse> responseObserver) {

        log.info("gRPC VerifyIdentity called for faydaId={}", request.getFaydaId());

        IdentityVerificationResult result =
                faydaServicePort.verifyIdentity(
                        request.getFaydaId(),
                        request.getPhone(),
                        request.getFullName()
                );

        VerifyIdentityResponse response =
                VerifyIdentityResponse.newBuilder()
                        .setVerified(result.isVerified())
                        .setMessage(result.getMessage())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void pullKycData(
            PullKycDataRequest request,
            StreamObserver<PullKycDataResponse> responseObserver) {

        log.info("gRPC PullKycData called for faydaId={}", request.getFaydaId());

        KycData kycData =
                faydaServicePort.pullKycData(request.getFaydaId());

        PullKycDataResponse response =
                PullKycDataResponse.newBuilder()
                        .setFaydaId(kycData.getFaydaId())
                        .setFullName(kycData.getFullName())
                        .setDateOfBirth(kycData.getDateOfBirth())
                        .setRegion(kycData.getRegion())
                        .setVerified(kycData.isVerified())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

