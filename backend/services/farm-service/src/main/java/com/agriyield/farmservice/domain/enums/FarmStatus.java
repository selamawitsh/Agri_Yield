package com.agriyield.farmservice.domain.enums;

public enum FarmStatus {
    PENDING_VERIFICATION,
    VERIFIED,
    ACTIVE,
    GROWING,
    HARVESTED,
    DORMANT,
    FAILED;

    public String getValue() {
        return this.name();
    }
}
