package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.escrowservice.grpc.*;
import com.agriyield.offtakerservice.application.port.outgoing.EscrowServicePort;
import com.agriyield.offtakerservice.domain.exception.BusinessException;
import io.grpc.StatusRuntimeException;
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
    public String lockBidDeposit(UUID bidId, UUID offtakerId, BigDecimal depositAmountEtb) {
        log.info("Locking bid deposit: bidId={} amount={}", bidId, depositAmountEtb);
        try {
            CreateEscrowRequest request = CreateEscrowRequest.newBuilder()
                    .setInvestmentId(bidId.toString())     // reusing investment_id field for bidId
                    .setFarmerId(offtakerId.toString())    // reusing farmer_id for offtaker
                    .setInvestorId(offtakerId.toString())
                    .setAmountEtb(depositAmountEtb.doubleValue())
                    .build();
            EscrowResponse response = escrowStub.createAndLock(request);
            return response.getId();
        } catch (StatusRuntimeException e) {
            log.error("Failed to lock bid deposit: {}", e.getMessage());
            throw new BusinessException("Escrow lock failed: " + e.getStatus().getDescription(), "ESCROW_LOCK_FAILED");
        }
    }

    @Override
    public void forfeitBidDeposit(UUID bidId) {
        log.info("Forfeiting/cancelling bid deposit: bidId={}", bidId);
        try {
            CancelEscrowRequest request = CancelEscrowRequest.newBuilder()
                    .setInvestmentId(bidId.toString())
                    .build();
            escrowStub.cancel(request);
        } catch (StatusRuntimeException e) {
            log.error("Failed to forfeit bid deposit: {}", e.getMessage());
        }
    }

    @Override
    public void processHarvestPayment(UUID farmId, UUID agreementId, BigDecimal totalPaymentEtb) {
        log.info("Processing harvest payment: farmId={} agreementId={} amount={}",
                farmId, agreementId, totalPaymentEtb);
        try {
            ReleasePartialRequest request = ReleasePartialRequest.newBuilder()
                    .setInvestmentId(agreementId.toString())
                    .setAmountEtb(totalPaymentEtb.doubleValue())
                    .setReleaseReason("HARVEST_CONFIRMED")
                    .build();
            escrowStub.releasePartial(request);
        } catch (StatusRuntimeException e) {
            log.error("Failed to process harvest payment: {}", e.getMessage());
            throw new BusinessException("Harvest payment failed: " + e.getStatus().getDescription(), "PAYMENT_FAILED");
        }
    }
}
