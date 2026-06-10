package com.agriyield.voucherservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.merchantservice.grpc.MerchantCategoriesResponse;
import com.agriyield.merchantservice.grpc.MerchantIdRequest;
import com.agriyield.merchantservice.grpc.MerchantLocationResponse;
import com.agriyield.merchantservice.grpc.MerchantServiceGrpc;
import com.agriyield.voucherservice.application.port.outgoing.MerchantServicePort;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class MerchantServiceGrpcClient implements MerchantServicePort {

    @GrpcClient("merchant-service")
    private MerchantServiceGrpc.MerchantServiceBlockingStub merchantStub;

    @Override
    public List<String> getMerchantCategories(UUID merchantId) {
        log.info("gRPC: getMerchantCategories for merchant: {}", merchantId);
        MerchantCategoriesResponse response = merchantStub.getMerchantCategories(
            MerchantIdRequest.newBuilder()
                .setMerchantId(merchantId.toString())
                .build());
        return response.getCategoriesList();
    }

    @Override
    public double[] getMerchantLocation(UUID merchantId) {
        log.info("gRPC: getMerchantLocation for merchant: {}", merchantId);
        MerchantLocationResponse response = merchantStub.getMerchantLocation(
            MerchantIdRequest.newBuilder()
                .setMerchantId(merchantId.toString())
                .build());
        return new double[]{response.getLatitude(), response.getLongitude()};
    }
}
