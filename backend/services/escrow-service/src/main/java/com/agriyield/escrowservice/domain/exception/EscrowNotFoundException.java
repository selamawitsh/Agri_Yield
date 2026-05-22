package com.agriyield.escrowservice.domain.exception;

public class EscrowNotFoundException extends BusinessException {

    public EscrowNotFoundException(String investmentId) {
        super("Escrow account not found for investment: " + investmentId, "ESCROW_NOT_FOUND");
    }
}