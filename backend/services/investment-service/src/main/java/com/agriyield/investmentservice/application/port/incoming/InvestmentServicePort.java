package com.agriyield.investmentservice.application.port.incoming;

import com.agriyield.investmentservice.domain.model.Investment;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InvestmentServicePort {

    Investment placeInvestment(UUID investorId,
                               UUID farmId,
                               UUID inputNeedId,
                               BigDecimal amountEtb,
                               String notes);

    Investment getById(UUID investmentId);

    List<Investment> getMyInvestments(UUID investorId);

    Investment cancel(UUID investmentId, UUID investorId, String reason);
}
