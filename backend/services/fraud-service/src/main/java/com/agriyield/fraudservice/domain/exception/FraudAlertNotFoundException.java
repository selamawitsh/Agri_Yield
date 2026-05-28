package com.agriyield.fraudservice.domain.exception;

public class FraudAlertNotFoundException extends RuntimeException {
    public FraudAlertNotFoundException(String id) {
        super("Fraud alert not found: " + id);
    }

    public String getErrorCode() { return "FRAUD_ALERT_NOT_FOUND"; }
}
