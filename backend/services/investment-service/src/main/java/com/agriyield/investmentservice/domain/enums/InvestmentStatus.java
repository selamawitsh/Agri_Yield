package com.agriyield.investmentservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvestmentStatus {
    PENDING("PENDING"),
    ESCROW_LOCKED("ESCROW_LOCKED"),
    ACTIVE("ACTIVE"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    FAILED("FAILED");

    private final String value;

    public static InvestmentStatus fromValue(String value) {
        for (InvestmentStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown investment status: " + value);
    }
}
