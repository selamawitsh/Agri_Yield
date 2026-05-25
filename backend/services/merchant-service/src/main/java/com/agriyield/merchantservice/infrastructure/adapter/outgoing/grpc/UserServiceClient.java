package com.agriyield.merchantservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.userservice.grpc.UserServiceGrpc;
import com.agriyield.userservice.grpc.UserServiceProto.KebeleRequest;
import com.agriyield.userservice.grpc.UserServiceProto.MerchantIdsResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserServiceClient {

    private final UserServiceGrpc.UserServiceBlockingStub blockingStub;

    public UserServiceClient() {

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 9091)
                        .usePlaintext()
                        .build();

        this.blockingStub =
                UserServiceGrpc.newBlockingStub(channel);
    }

    public List<UUID> getMerchantIdsByKebele(
            String kebeleCode
    ) {

        KebeleRequest request =
                KebeleRequest.newBuilder()
                        .setKebeleCode(kebeleCode)
                        .build();

        MerchantIdsResponse response =
                blockingStub.getMerchantIdsByKebele(request);

        return response
                .getMerchantIdsList()
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
}