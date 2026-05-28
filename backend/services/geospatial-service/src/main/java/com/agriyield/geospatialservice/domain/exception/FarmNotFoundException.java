package com.agriyield.geospatialservice.domain.exception;

public class FarmNotFoundException extends RuntimeException {
    public FarmNotFoundException(String farmId) {
        super("Farm not found: " + farmId);
    }
    public String getErrorCode() { return "FARM_NOT_FOUND"; }
}
