package com.agriyield.farmservice.domain.enums;

public enum CropCycleStatus {
    PLANNING,
    FUNDED,
    PLANTED,
    GROWING,
    HARVESTED,
    FAILED;

    public String getValue() {
        return this.name();
    }
}
