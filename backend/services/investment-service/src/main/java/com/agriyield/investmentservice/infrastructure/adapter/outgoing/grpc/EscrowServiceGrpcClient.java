package com.agriyield.investmentservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.escrowservice.grpc.CancelEscrowRequest;
import com.agriyield.escrowservice.grpc.CreateEscrowRequest;
import com.agriyield.escrowservice.grpc.EscrowServiceGrpc;
import com.agriyield.investmentservice.application.port.outgoing.EscrowServicePort;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class EscrowServiceGrpcClient implements EscrowServicePort {

    @GrpcClient("escrow-service")
    private EscrowServiceGrpc.EscrowServiceBlockingStub escrowStub;

    @Override
    public void createAndLock(UUID investmentId,
                              UUID farmerId,
                              UUID investorId,
                              BigDecimal amountEtb) {
        log.info("gRPC: createAndLock escrow for investment: {}", investmentId);
        CreateEscrowRequest request = CreateEscrowRequest.newBuilder()
            .setInvestmentId(investmentId.toString())
            .setFarmerId(farmerId.toString())
            .setInvestorId(investorId.toString())
            .setAmountEtb(amountEtb.doubleValue())
            .build();
        escrowStub.createAndLock(request);
    }

    @Override
    public void cancel(UUID investmentId) {
        log.info("gRPC: cancel escrow for investment: {}", investmentId);
        CancelEscrowRequest request = CancelEscrowRequest.newBuilder()
            .setInvestmentId(investmentId.toString())
            .build();
        escrowStub.cancel(request);
    }
}
