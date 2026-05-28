package com.agriyield.fraudservice.domain.enums;

public enum FraudAlertType {
    DUPLICATE_VOUCHER_REDEMPTION("DUPLICATE_VOUCHER_REDEMPTION"),
    INVALID_QR_SIGNATURE("INVALID_QR_SIGNATURE"),
    GPS_MISMATCH("GPS_MISMATCH"),
    EXIF_METADATA_MISMATCH("EXIF_METADATA_MISMATCH"),
    SUSPICIOUS_GPS_MOVEMENT("SUSPICIOUS_GPS_MOVEMENT"),
    MERCHANT_INELIGIBLE("MERCHANT_INELIGIBLE"),
    HIGH_FRAUD_SCORE("HIGH_FRAUD_SCORE"),
    SUSPICIOUS_ACCOUNT("SUSPICIOUS_ACCOUNT");

    private final String value;

    FraudAlertType(String value) { this.value = value; }

    public String getValue() { return value; }
}
