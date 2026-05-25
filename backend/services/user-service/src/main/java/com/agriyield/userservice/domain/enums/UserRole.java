package com.agriyield.userservice.domain.enums;

public enum UserRole {
    FARMER, INVESTOR, MERCHANT, OFF_TAKER, AGENT, ADMIN;

    public String getValue() { return this.name(); }

    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(value)) return role;
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
