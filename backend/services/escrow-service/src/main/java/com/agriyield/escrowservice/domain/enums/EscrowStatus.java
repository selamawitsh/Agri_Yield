
package com.agriyield.escrowservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EscrowStatus {
    PENDING("PENDING"),
    LOCKED("LOCKED"),
    PARTIALLY_RELEASED("PARTIALLY_RELEASED"),
    FULLY_RELEASED("FULLY_RELEASED"),
    CANCELLED("CANCELLED"),
    EXPIRED("EXPIRED");

    private final String value;

    public static EscrowStatus fromValue(String value) {
        for (EscrowStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown escrow status: " + value);
    }
}
