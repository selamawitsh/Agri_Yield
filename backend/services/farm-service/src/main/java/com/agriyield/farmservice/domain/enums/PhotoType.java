package com.agriyield.farmservice.domain.enums;

public enum PhotoType {
    REGISTRATION,
    CROP_HEALTH,
    HARVEST;

    public String getValue() {
        return this.name();
    }
}
