package com.agriyield.voucherservice.application.port.outgoing;

import java.util.UUID;

public interface InvestmentServicePort {

    record InvestmentContext(
        String investmentId,
        String investorId,
        String farmId,
        String farmerId,
        String inputNeedId,
        double amountEtb,
        String status
    ) {}

    InvestmentContext getInvestmentById(UUID investmentId);

    boolean verifyInvestmentFunded(UUID investmentId);
}
