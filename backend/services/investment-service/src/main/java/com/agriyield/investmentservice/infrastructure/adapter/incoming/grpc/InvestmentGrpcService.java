package com.agriyield.investmentservice.infrastructure.adapter.incoming.grpc;

import com.agriyield.investmentservice.application.port.outgoing.InvestmentRepositoryPort;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.grpc.InvestmentServiceGrpc;
import com.agriyield.investmentservice.grpc.InvestmentServiceProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class InvestmentGrpcService extends InvestmentServiceGrpc.InvestmentServiceImplBase {

    private final InvestmentRepositoryPort investmentRepository;

    @Override
    public void getInvestmentById(
            InvestmentServiceProto.InvestmentIdRequest request,
            StreamObserver<InvestmentServiceProto.InvestmentResponse> responseObserver) {
        log.info("gRPC getInvestmentById: {}", request.getInvestmentId());
        try {
            Investment investment = investmentRepository
                .findById(UUID.fromString(request.getInvestmentId()))
                .orElseThrow(() -> new RuntimeException(
                    "Investment not found: " + request.getInvestmentId()));
            responseObserver.onNext(toResponse(investment));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getInvestmentById failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getInvestmentByFarmId(
            InvestmentServiceProto.FarmIdRequest request,
            StreamObserver<InvestmentServiceProto.InvestmentResponse> responseObserver) {
        log.info("gRPC getInvestmentByFarmId: {}", request.getFarmId());
        try {
            Investment investment = investmentRepository
                .findByFarmId(UUID.fromString(request.getFarmId()))
                .orElseThrow(() -> new RuntimeException(
                    "No investment found for farm: " + request.getFarmId()));
            responseObserver.onNext(toResponse(investment));
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC getInvestmentByFarmId failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void verifyInvestmentFunded(
            InvestmentServiceProto.InvestmentIdRequest request,
            StreamObserver<InvestmentServiceProto.FundedResponse> responseObserver) {
        log.info("gRPC verifyInvestmentFunded: {}", request.getInvestmentId());
        try {
            Investment investment = investmentRepository
                .findById(UUID.fromString(request.getInvestmentId()))
                .orElseThrow(() -> new RuntimeException(
                    "Investment not found: " + request.getInvestmentId()));

            boolean isFunded = investment.getStatus().getValue().equals("ESCROW_LOCKED")
                || investment.getStatus().getValue().equals("ACTIVE")
                || investment.getStatus().getValue().equals("COMPLETED");

            InvestmentServiceProto.FundedResponse response =
                InvestmentServiceProto.FundedResponse.newBuilder()
                    .setIsFunded(isFunded)
                    .setInvestmentId(investment.getId().toString())
                    .setStatus(investment.getStatus().getValue())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("gRPC verifyInvestmentFunded failed: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private InvestmentServiceProto.InvestmentResponse toResponse(Investment investment) {
        return InvestmentServiceProto.InvestmentResponse.newBuilder()
            .setId(investment.getId().toString())
            .setInvestorId(investment.getInvestorId().toString())
            .setFarmId(investment.getFarmId().toString())
            .setFarmerId(investment.getFarmerId().toString())
            .setInputNeedId(investment.getInputNeedId().toString())
            .setAmountEtb(investment.getAmountEtb().doubleValue())
            .setStatus(investment.getStatus().getValue())
            .setCropType(investment.getCropType())
            .setRegion(investment.getRegion())
            .setCreatedAt(investment.getCreatedAt().toString())
            .build();
    }
}
