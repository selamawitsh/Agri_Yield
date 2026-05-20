package com.agriyield.farmservice.domain.enums;

public enum CropType {
    WHEAT,
    TEFF,
    BARLEY,
    MAIZE,
    SORGHUM,
    COFFEE,
    BEANS,
    MILLET;

    public String getValue() {
        return this.name();
    }

    public static CropType fromValue(String value) {
        for (CropType type : CropType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown crop type: " + value);
    }
}
