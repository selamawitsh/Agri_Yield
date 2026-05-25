package com.agriyield.userservice.domain.enums;

public enum KycStatus {
    PENDING, VERIFIED, REJECTED;

    public String getValue() { return this.name(); }
}
