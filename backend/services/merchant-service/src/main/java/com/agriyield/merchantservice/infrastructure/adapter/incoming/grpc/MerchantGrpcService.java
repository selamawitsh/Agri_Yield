package com.agriyield.merchantservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.merchantservice.application.port.incoming.MerchantServicePort;
import com.agriyield.merchantservice.domain.exception.BusinessException;
import com.agriyield.merchantservice.domain.model.Product;
import com.agriyield.merchantservice.grpc.*;
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
public class MerchantGrpcService extends MerchantServiceGrpc.MerchantServiceImplBase {

    private final MerchantServicePort merchantService;

    @Override
    public void getMerchantCategories(MerchantIdRequest request,
            StreamObserver<MerchantCategoriesResponse> responseObserver) {
        UUID merchantId = UUID.fromString(request.getMerchantId());
        log.info("gRPC GetMerchantCategories merchantId={}", merchantId);
        MerchantCategoriesResponse response = MerchantCategoriesResponse.newBuilder()
            .addAllCategories(merchantService.getMerchantCategories(merchantId))
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getMerchantLocation(MerchantIdRequest request,
            StreamObserver<MerchantLocationResponse> responseObserver) {
        UUID merchantId = UUID.fromString(request.getMerchantId());
        log.info("gRPC GetMerchantLocation merchantId={}", merchantId);
        double[] loc = merchantService.getMerchantLocation(merchantId);
        responseObserver.onNext(MerchantLocationResponse.newBuilder()
            .setLatitude(loc[0]).setLongitude(loc[1]).build());
        responseObserver.onCompleted();
    }

    @Override
    public void verifyMerchantActive(MerchantIdRequest request,
            StreamObserver<MerchantActiveResponse> responseObserver) {
        UUID merchantId = UUID.fromString(request.getMerchantId());
        log.info("gRPC VerifyMerchantActive merchantId={}", merchantId);
        responseObserver.onNext(MerchantActiveResponse.newBuilder()
            .setActive(merchantService.isMerchantActive(merchantId)).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getRegionalPriceIndex(RegionalPriceIndexRequest request,
            StreamObserver<RegionalPriceIndexResponse> responseObserver) {
        log.info("gRPC GetRegionalPriceIndex kebele={} category={}",
            request.getKebeleCode(), request.getCategory());
        double price = merchantService.getRegionalPriceIndex(
            request.getKebeleCode(), request.getCategory());
        responseObserver.onNext(RegionalPriceIndexResponse.newBuilder()
            .setPriceIndex(price).build());
        responseObserver.onCompleted();
    }

    /**
     * SRS §3.5.3 extension — Check inventory before voucher redemption.
     * Called by voucher-service between Check #3 and Check #4.
     * Returns available quantity and unit, or error status if insufficient.
     */
    @Override
    public void checkInventory(CheckInventoryRequest request,
            StreamObserver<CheckInventoryResponse> responseObserver) {
        UUID merchantId = UUID.fromString(request.getMerchantId());
        String category  = request.getCategory();
        BigDecimal qty   = BigDecimal.valueOf(request.getRequiredQuantity());
        log.info("gRPC CheckInventory merchant={} category={} qty={}", merchantId, category, qty);
        try {
            Product product = merchantService.checkInventoryForRedemption(merchantId, category, qty);
            responseObserver.onNext(CheckInventoryResponse.newBuilder()
                .setAvailable(true)
                .setProductName(product.getProductName())
                .setAvailableQuantity(product.getQuantityInStock().doubleValue())
                .setUnit(product.getUnit())
                .setProductId(product.getId().toString())
                .build());
            responseObserver.onCompleted();
        } catch (BusinessException e) {
            responseObserver.onNext(CheckInventoryResponse.newBuilder()
                .setAvailable(false)
                .setErrorCode(e.getErrorCode())
                .setErrorMessage(e.getMessage())
                .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC CheckInventory failed: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**
     * Called by voucher-service after successful redemption to deduct stock.
     */
    @Override
    public void deductInventory(DeductInventoryRequest request,
            StreamObserver<DeductInventoryResponse> responseObserver) {
        UUID merchantId = UUID.fromString(request.getMerchantId());
        String category  = request.getCategory();
        BigDecimal qty   = BigDecimal.valueOf(request.getQuantity());
        log.info("gRPC DeductInventory merchant={} category={} qty={}", merchantId, category, qty);
        try {
            merchantService.deductInventory(merchantId, category, qty);
            responseObserver.onNext(DeductInventoryResponse.newBuilder()
                .setSuccess(true).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC DeductInventory failed: {}", e.getMessage());
            responseObserver.onNext(DeductInventoryResponse.newBuilder()
                .setSuccess(false)
                .setErrorMessage(e.getMessage()).build());
            responseObserver.onCompleted();
        }
    }
}
