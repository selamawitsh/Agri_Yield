package com.agriyield.merchantservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterMerchantRequest {

    @NotBlank(message = "Business name is required")
    private String businessName;

    @NotBlank(message = "Business license number is required")
    private String businessLicenseNumber;

    @NotNull(message = "Store GPS latitude is required")
    private Double storeGpsLat;

    @NotNull(message = "Store GPS longitude is required")
    private Double storeGpsLng;

    @NotBlank(message = "Telebirr account is required")
    private String telebirrAccount;

    private String kebeleCode;
}
