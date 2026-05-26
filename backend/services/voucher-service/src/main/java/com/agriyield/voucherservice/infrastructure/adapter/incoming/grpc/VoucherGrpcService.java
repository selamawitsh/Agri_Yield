package com.agriyield.voucherservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.grpc.VoucherServiceGrpc;
import com.agriyield.voucherservice.grpc.VoucherServiceProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class VoucherGrpcService extends VoucherServiceGrpc.VoucherServiceImplBase {

    private final VoucherServicePort voucherService;

    @Override
    public void getVoucherById(
            VoucherServiceProto.VoucherIdRequest request,
            StreamObserver<VoucherServiceProto.VoucherResponse> responseObserver) {

        log.info("gRPC getVoucherById: {}", request.getVoucherId());

        try {

            Voucher voucher = voucherService.getById(
                    UUID.fromString(request.getVoucherId()));

            responseObserver.onNext(toResponse(voucher));
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC getVoucherById failed", e);

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getVouchersByFarmId(
            VoucherServiceProto.FarmIdRequest request,
            StreamObserver<VoucherServiceProto.VoucherListResponse> responseObserver) {

        log.info("gRPC getVouchersByFarmId: {}", request.getFarmId());

        try {

            List<Voucher> vouchers = voucherService.getByFarmId(
                    UUID.fromString(request.getFarmId()));

            VoucherServiceProto.VoucherListResponse response =
                    VoucherServiceProto.VoucherListResponse.newBuilder()
                            .addAllVouchers(
                                    vouchers.stream()
                                            .map(this::toResponse)
                                            .collect(Collectors.toList())
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC getVouchersByFarmId failed", e);

            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void verifyVoucher(
            VoucherServiceProto.VoucherCodeRequest request,
            StreamObserver<VoucherServiceProto.VoucherVerifyResponse> responseObserver) {

        log.info("gRPC verifyVoucher: {}", request.getVoucherCode());

        try {

            Voucher voucher = voucherService.getByCode(
                    request.getVoucherCode());

            boolean isValid = voucher.isValid();

            VoucherServiceProto.VoucherVerifyResponse response =
                    VoucherServiceProto.VoucherVerifyResponse.newBuilder()
                            .setIsValid(isValid)
                            .setVoucherId(voucher.getId().toString())
                            .setStatus(voucher.getStatus().getValue())
                            .setMessage(
                                    isValid
                                            ? "Voucher is valid"
                                            : "Voucher is not valid"
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            log.error("gRPC verifyVoucher failed", e);

            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    private VoucherServiceProto.VoucherResponse toResponse(Voucher v) {

        VoucherServiceProto.VoucherResponse.Builder builder =
                VoucherServiceProto.VoucherResponse.newBuilder()
                        .setId(v.getId().toString())
                        .setVoucherCode(v.getVoucherCode())
                        .setFarmId(v.getFarmId().toString())
                        .setFarmerId(v.getFarmerId().toString())
                        .setInvestmentId(v.getInvestmentId().toString())
                        .setInputNeedId(v.getInputNeedId().toString())
                        .setProductName(v.getProductName())
                        .setProductCategory(v.getProductCategory().getValue())
                        .setAmountEtb(v.getAmountEtb().doubleValue())
                        .setStatus(v.getStatus().getValue());

        if (v.getIssuedAt() != null) {
            builder.setIssuedAt(v.getIssuedAt().toString());
        }

        if (v.getExpiresAt() != null) {
            builder.setExpiresAt(v.getExpiresAt().toString());
        }

        if (v.getCreatedAt() != null) {
            builder.setCreatedAt(v.getCreatedAt().toString());
        }

        return builder.build();
    }
}