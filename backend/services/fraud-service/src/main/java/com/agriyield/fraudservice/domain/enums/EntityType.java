package com.agriyield.fraudservice.domain.enums;

public enum EntityType {
    FARMER("FARMER"),
    MERCHANT("MERCHANT"),
    INVESTOR("INVESTOR");

    private final String value;

    EntityType(String value) { this.value = value; }

    public String getValue() { return value; }

    public static EntityType fromValue(String value) {
        for (EntityType t : values()) {
            if (t.value.equalsIgnoreCase(value)) return t;
        }
        return FARMER;
    }
}
