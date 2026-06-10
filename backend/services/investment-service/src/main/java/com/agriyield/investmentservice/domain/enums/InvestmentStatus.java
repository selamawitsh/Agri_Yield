package com.agriyield.investmentservice.domain.enums;

public enum InvestmentStatus {
    PENDING("PENDING"),
    ESCROW_LOCKED("ESCROW_LOCKED"),
    ACTIVE("ACTIVE"),
    COMPLETED("COMPLETED"),
    REFUNDED("REFUNDED"),
    CANCELLED("CANCELLED"),
    FAILED("FAILED");

    private final String value;

    InvestmentStatus(String value) { this.value = value; }

    public String getValue() { return value; }

    /** Used by InvestmentEntityMapper to convert DB string → enum */
    public static InvestmentStatus fromValue(String value) {
        if (value == null) return PENDING;
        for (InvestmentStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown InvestmentStatus: " + value);
    }
}
