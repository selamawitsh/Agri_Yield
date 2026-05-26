package com.agriyield.voucherservice.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductCategory {
    SEED("SEED"),
    FERTILIZER("FERTILIZER"),
    PESTICIDE("PESTICIDE"),
    TOOL("TOOL"),
    OTHER("OTHER");

    private final String value;

    public static ProductCategory fromValue(String value) {
        for (ProductCategory c : values()) {
            if (c.value.equalsIgnoreCase(value)) return c;
        }
        return OTHER;
    }
}
