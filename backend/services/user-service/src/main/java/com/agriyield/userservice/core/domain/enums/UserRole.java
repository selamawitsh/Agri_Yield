package com.agriyield.userservice.core.domain.enums;

public enum UserRole {
    FARMER("FARMER"),
    INVESTOR("INVESTOR"),
    MERCHANT("MERCHANT"),
    OFF_TAKER("OFF_TAKER"),
    AGENT("AGENT"),
    ADMIN("ADMIN");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}