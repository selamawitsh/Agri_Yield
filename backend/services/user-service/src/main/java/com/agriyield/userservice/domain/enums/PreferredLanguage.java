package com.agriyield.userservice.domain.enums;

public enum PreferredLanguage {
    AM("am"), OM("om"), TI("ti"), EN("en");

    private final String code;

    PreferredLanguage(String code) { this.code = code; }

    public String getCode() { return code; }

    public static PreferredLanguage fromCode(String code) {
        for (PreferredLanguage lang : PreferredLanguage.values()) {
            if (lang.code.equalsIgnoreCase(code) ||
                lang.name().equalsIgnoreCase(code)) return lang;
        }
        return AM;
    }
}
