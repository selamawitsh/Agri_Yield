package com.agriyield.userservice.core.domain.enums;

public enum OtpPurpose {
    REGISTRATION("REGISTRATION"),
    LOGIN("LOGIN"),
    PASSWORD_RESET("PASSWORD_RESET"),
    BANK_VERIFY("BANK_VERIFY");

    private final String value;

    OtpPurpose(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}