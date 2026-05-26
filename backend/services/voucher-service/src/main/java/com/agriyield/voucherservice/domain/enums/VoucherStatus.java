package com.agriyield.voucherservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VoucherStatus {
    GENERATED("GENERATED"),
    ISSUED("ISSUED"),
    REDEEMED("REDEEMED"),
    EXPIRED("EXPIRED"),
    CANCELLED("CANCELLED");

    private final String value;

    public static VoucherStatus fromValue(String value) {
        for (VoucherStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown voucher status: " + value);
    }
}
