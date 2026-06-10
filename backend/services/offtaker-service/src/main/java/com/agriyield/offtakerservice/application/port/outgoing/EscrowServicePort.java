package com.agriyield.offtakerservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface EscrowServicePort {
    String lockBidDeposit(UUID bidId, UUID offtakerId, BigDecimal depositAmountEtb);
    void forfeitBidDeposit(UUID bidId);
    void processHarvestPayment(UUID farmId, UUID agreementId, BigDecimal totalPaymentEtb);
}
