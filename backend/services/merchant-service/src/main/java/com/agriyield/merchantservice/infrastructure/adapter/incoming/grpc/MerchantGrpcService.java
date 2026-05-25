package com.agriyield.merchantservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.merchantservice.application.port.incoming.MerchantServicePort;
import com.agriyield.merchantservice.grpc.MerchantActiveResponse;
import com.agriyield.merchantservice.grpc.MerchantCategoriesResponse;
import com.agriyield.merchantservice.grpc.MerchantIdRequest;
import com.agriyield.merchantservice.grpc.MerchantLocationResponse;
import com.agriyield.merchantservice.grpc.MerchantServiceGrpc;
import com.agriyield.merchantservice.grpc.RegionalPriceIndexRequest;
import com.agriyield.merchantservice.grpc.RegionalPriceIndexResponse;

import io.grpc.stub.StreamObserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class MerchantGrpcService extends MerchantServiceGrpc.MerchantServiceImplBase {

    private final MerchantServicePort merchantService;

    @Override
    public void getMerchantCategories(
            MerchantIdRequest request,
            StreamObserver<MerchantCategoriesResponse> responseObserver
    ) {

        UUID merchantId = UUID.fromString(request.getMerchantId());

        log.info("gRPC GetMerchantCategories called for merchantId={}", merchantId);

        MerchantCategoriesResponse response =
                MerchantCategoriesResponse.newBuilder()
                        .addAllCategories(
                                merchantService.getMerchantCategories(merchantId)
                        )
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getMerchantLocation(
            MerchantIdRequest request,
            StreamObserver<MerchantLocationResponse> responseObserver
    ) {

        UUID merchantId = UUID.fromString(request.getMerchantId());

        log.info("gRPC GetMerchantLocation called for merchantId={}", merchantId);

        double[] location =
                merchantService.getMerchantLocation(merchantId);

        MerchantLocationResponse response =
                MerchantLocationResponse.newBuilder()
                        .setLatitude(location[0])
                        .setLongitude(location[1])
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void verifyMerchantActive(
            MerchantIdRequest request,
            StreamObserver<MerchantActiveResponse> responseObserver
    ) {

        UUID merchantId = UUID.fromString(request.getMerchantId());

        log.info("gRPC VerifyMerchantActive called for merchantId={}", merchantId);

        boolean active =
                merchantService.isMerchantActive(merchantId);

        MerchantActiveResponse response =
                MerchantActiveResponse.newBuilder()
                        .setActive(active)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getRegionalPriceIndex(
            RegionalPriceIndexRequest request,
            StreamObserver<RegionalPriceIndexResponse> responseObserver
    ) {

        log.info(
                "gRPC GetRegionalPriceIndex called: kebele={} category={}",
                request.getKebeleCode(),
                request.getCategory()
        );

        double priceIndex =
                merchantService.getRegionalPriceIndex(
                        request.getKebeleCode(),
                        request.getCategory()
                );

        RegionalPriceIndexResponse response =
                RegionalPriceIndexResponse.newBuilder()
                        .setPriceIndex(priceIndex)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}