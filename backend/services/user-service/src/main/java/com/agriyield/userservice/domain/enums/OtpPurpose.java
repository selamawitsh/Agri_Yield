package com.agriyield.userservice.domain.enums;

public enum OtpPurpose {
    REGISTRATION, LOGIN, PASSWORD_RESET, BANK_VERIFY;

    public String getValue() { return this.name(); }
}
