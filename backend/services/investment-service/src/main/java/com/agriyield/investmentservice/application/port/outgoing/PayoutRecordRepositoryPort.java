package com.agriyield.investmentservice.application.port.outgoing;

import com.agriyield.investmentservice.domain.model.PayoutRecord;

import java.util.List;
import java.util.UUID;

public interface PayoutRecordRepositoryPort {

    PayoutRecord save(PayoutRecord payoutRecord);

    List<PayoutRecord> findByInvestorId(UUID investorId);

    List<PayoutRecord> findByInvestmentId(UUID investmentId);
}
