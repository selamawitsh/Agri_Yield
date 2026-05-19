package com.agriyield.userservice.core.domain.enums;

public enum KycStatus {
    PENDING("PENDING"),
    VERIFIED("VERIFIED"),
    REJECTED("REJECTED");

    private final String value;

    KycStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}