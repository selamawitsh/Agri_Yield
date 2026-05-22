package com.agriyield.escrowservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.escrowservice.application.port.incoming.EscrowServicePort;
import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.domain.model.EscrowRelease;
import com.agriyield.escrowservice.grpc.EscrowServiceGrpc;
import com.agriyield.escrowservice.grpc.CancelEscrowRequest;
import com.agriyield.escrowservice.grpc.CreateEscrowRequest;
import com.agriyield.escrowservice.grpc.EscrowResponse;
import com.agriyield.escrowservice.grpc.GetEscrowRequest;
import com.agriyield.escrowservice.grpc.ReleasePartialRequest;
import com.agriyield.escrowservice.grpc.ReleaseResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class EscrowGrpcService extends EscrowServiceGrpc.EscrowServiceImplBase {

    private final EscrowServicePort escrowService;

    @Override
    public void createAndLock(CreateEscrowRequest request,
                              StreamObserver<EscrowResponse> responseObserver) {
        log.info("gRPC createAndLock for investment: {}", request.getInvestmentId());
        try {
            EscrowAccount account = escrowService.createAndLock(
                UUID.fromString(request.getInvestmentId()),
                UUID.fromString(request.getFarmerId()),
                UUID.fromString(request.getInvestorId()),
                BigDecimal.valueOf(request.getAmountEtb()));
            responseObserver.onNext(toResponse(account));
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Invalid UUID format").asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC createAndLock failed: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void releasePartial(ReleasePartialRequest request,
                               StreamObserver<ReleaseResponse> responseObserver) {
        log.info("gRPC releasePartial for investment: {}", request.getInvestmentId());
        try {
            EscrowRelease release = escrowService.releasePartial(
                UUID.fromString(request.getInvestmentId()),
                request.getVoucherId() != null && !request.getVoucherId().isBlank()
                    ? UUID.fromString(request.getVoucherId()) : null,
                BigDecimal.valueOf(request.getAmountEtb()),
                request.getReleaseReason());

            ReleaseResponse response = ReleaseResponse.newBuilder()
                .setReleaseId(release.getId().toString())
                .setEscrowId(release.getEscrowAccountId().toString())
                .setVoucherId(release.getVoucherId() != null
                    ? release.getVoucherId().toString() : "")
                .setAmountEtb(release.getAmountEtb().doubleValue())
                .setReleaseReason(release.getReleaseReason() != null
                    ? release.getReleaseReason() : "")
                .setReleasedAt(release.getReleasedAt().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("gRPC releasePartial failed: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void cancel(CancelEscrowRequest request,
                       StreamObserver<EscrowResponse> responseObserver) {
        log.info("gRPC cancel escrow for investment: {}", request.getInvestmentId());
        try {
            EscrowAccount account = escrowService.cancel(
                UUID.fromString(request.getInvestmentId()));
            responseObserver.onNext(toResponse(account));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC cancel failed: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getByInvestmentId(GetEscrowRequest request,
                                  StreamObserver<EscrowResponse> responseObserver) {
        log.info("gRPC getByInvestmentId: {}", request.getInvestmentId());
        try {
            EscrowAccount account = escrowService.getByInvestmentId(
                UUID.fromString(request.getInvestmentId()));
            responseObserver.onNext(toResponse(account));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getByInvestmentId failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private EscrowResponse toResponse(EscrowAccount account) {
        EscrowResponse.Builder builder = EscrowResponse.newBuilder()
            .setId(account.getId().toString())
            .setInvestmentId(account.getInvestmentId().toString())
            .setFarmerId(account.getFarmerId().toString())
            .setInvestorId(account.getInvestorId().toString())
            .setTotalAmountEtb(account.getTotalAmountEtb().doubleValue())
            .setLockedAmountEtb(account.getLockedAmountEtb().doubleValue())
            .setReleasedAmountEtb(account.getReleasedAmountEtb().doubleValue())
            .setStatus(account.getStatus().getValue())
            .setCreatedAt(account.getCreatedAt().toString());
        if (account.getLockExpiresAt() != null) {
            builder.setLockExpiresAt(account.getLockExpiresAt().toString());
        }
        return builder.build();
    }
}
