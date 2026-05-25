package com.agriyield.userservice.domain.enums;

public enum AccountStatus {
    ACTIVE, SUSPENDED, DELETED, PENDING_VERIFICATION;

    public String getValue() { return this.name(); }
}
