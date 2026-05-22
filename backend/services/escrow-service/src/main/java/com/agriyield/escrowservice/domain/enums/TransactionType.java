package com.agriyield.escrowservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    LOCK("LOCK"),
    RELEASE("RELEASE"),
    PARTIAL_RELEASE("PARTIAL_RELEASE"),
    REFUND("REFUND"),
    EXPIRY("EXPIRY");

    private final String value;

    public static TransactionType fromValue(String value) {
        for (TransactionType t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        throw new IllegalArgumentException("Unknown transaction type: " + value);
    }
}