package com.agriyield.investmentservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ListingStatus {
    OPEN("OPEN"),
    PARTIALLY_FUNDED("PARTIALLY_FUNDED"),
    FULLY_FUNDED("FULLY_FUNDED"),
    FUNDING_FAILED("FUNDING_FAILED"),
    ACTIVE("ACTIVE"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED");

    private final String value;

    public static ListingStatus fromValue(String value) {
        for (ListingStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown listing status: " + value);
    }
}
