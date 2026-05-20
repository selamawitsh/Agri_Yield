package com.agriyield.farmservice.domain.enums;

public enum ProductCategory {
    SEED,
    FERTILIZER,
    PESTICIDE,
    TOOL,
    OTHER;

    public String getValue() {
        return this.name();
    }

    public static ProductCategory fromValue(String value) {
        for (ProductCategory category : ProductCategory.values()) {
            if (category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown product category: " + value);
    }
}
