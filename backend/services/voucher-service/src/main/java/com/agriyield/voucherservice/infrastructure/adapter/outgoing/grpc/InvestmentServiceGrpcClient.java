package com.agriyield.voucherservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.investmentservice.grpc.InvestmentServiceGrpc;
import com.agriyield.investmentservice.grpc.InvestmentServiceProto;
import com.agriyield.voucherservice.application.port.outgoing.InvestmentServicePort;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class InvestmentServiceGrpcClient implements InvestmentServicePort {

    @GrpcClient("investment-service")
    private InvestmentServiceGrpc.InvestmentServiceBlockingStub investmentStub;

    @Override
    public InvestmentContext getInvestmentById(UUID investmentId) {
        log.info("gRPC: getInvestmentById: {}", investmentId);
        try {
            InvestmentServiceProto.InvestmentResponse response = investmentStub.getInvestmentById(
                InvestmentServiceProto.InvestmentIdRequest.newBuilder()
                    .setInvestmentId(investmentId.toString())
                    .build());
            return new InvestmentContext(
                response.getId(),
                response.getInvestorId(),
                response.getFarmId(),
                response.getFarmerId(),
                response.getInputNeedId(),
                response.getAmountEtb(),
                response.getStatus());
        } catch (Exception e) {
            log.error("gRPC: getInvestmentById failed: {}", e.getMessage());
            throw new RuntimeException("Could not retrieve investment: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyInvestmentFunded(UUID investmentId) {
        log.info("gRPC: verifyInvestmentFunded: {}", investmentId);
        try {
            InvestmentServiceProto.FundedResponse response = investmentStub.verifyInvestmentFunded(
                InvestmentServiceProto.InvestmentIdRequest.newBuilder()
                    .setInvestmentId(investmentId.toString())
                    .build());
            return response.getIsFunded();
        } catch (Exception e) {
            log.error("gRPC: verifyInvestmentFunded failed: {}", e.getMessage());
            return false;
        }
    }
}
