package com.agriyield.userservice.core.domain.enums;

public enum PreferredLanguage {
    AM("am", "Amharic"),
    OM("om", "Oromiffa"),
    TI("ti", "Tigrinya"),
    EN("en", "English");

    private final String code;
    private final String name;

    PreferredLanguage(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PreferredLanguage fromCode(String code) {
        for (PreferredLanguage lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        return EN; // default to English
    }
}