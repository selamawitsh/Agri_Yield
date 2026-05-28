package com.agriyield.fraudservice.domain.enums;

public enum FraudSeverity {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL");

    private final String value;

    FraudSeverity(String value) { this.value = value; }

    public String getValue() { return value; }

    public static FraudSeverity fromValue(String value) {
        for (FraudSeverity s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return LOW;
    }

    public static FraudSeverity fromScore(int score) {
        if (score >= 90) return CRITICAL;
        if (score >= 70) return HIGH;
        if (score >= 40) return MEDIUM;
        return LOW;
    }
}
