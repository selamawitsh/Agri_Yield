package com.agriyield.farmservice.domain.exception;

public class FarmNotFoundException extends BusinessException {

    public FarmNotFoundException(String farmId) {
        super("Farm not found with id: " + farmId, "FARM_NOT_FOUND");
    }
}
