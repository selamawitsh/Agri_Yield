package com.agriyield.voucherservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.escrowservice.grpc.EscrowServiceGrpc;
import com.agriyield.escrowservice.grpc.ReleasePartialRequest;
import com.agriyield.voucherservice.application.port.outgoing.EscrowServicePort;
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
    public void releasePartial(UUID investmentId,
                               UUID voucherId,
                               BigDecimal amountEtb,
                               String releaseReason) {
        log.info("gRPC: releasePartial escrow for investment: {}, voucher: {}",
            investmentId, voucherId);
        ReleasePartialRequest request = ReleasePartialRequest.newBuilder()
            .setInvestmentId(investmentId.toString())
            .setVoucherId(voucherId.toString())
            .setAmountEtb(amountEtb.doubleValue())
            .setReleaseReason(releaseReason != null ? releaseReason : "Voucher redeemed")
            .build();
        escrowStub.releasePartial(request);
        log.info("gRPC: escrow released {} ETB for voucher: {}", amountEtb, voucherId);
    }
}
