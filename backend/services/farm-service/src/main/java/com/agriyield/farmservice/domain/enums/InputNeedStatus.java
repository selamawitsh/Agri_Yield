package com.agriyield.farmservice.domain.enums;

public enum InputNeedStatus {
    OPEN,
    PARTIALLY_FUNDED,
    FULLY_FUNDED,
    CANCELLED;

    public String getValue() {
        return this.name();
    }
}
