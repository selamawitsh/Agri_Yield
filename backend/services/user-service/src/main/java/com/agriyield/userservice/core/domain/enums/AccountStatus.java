package com.agriyield.userservice.core.domain.enums;

public enum AccountStatus {
    PENDING_VERIFICATION("PENDING_VERIFICATION"),
    ACTIVE("ACTIVE"),
    SUSPENDED("SUSPENDED"),
    DELETED("DELETED");
    
    private final String value;
    
    AccountStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
