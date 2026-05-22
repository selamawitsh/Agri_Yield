package com.agriyield.investmentservice.domain.exception;

public class InvestmentNotFoundException extends BusinessException {

    public InvestmentNotFoundException(String id) {
        super("Investment not found: " + id, "INVESTMENT_NOT_FOUND");
    }
}
