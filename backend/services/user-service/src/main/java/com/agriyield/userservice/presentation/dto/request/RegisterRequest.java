package com.agriyield.userservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Fayda ID is required")
    private String faydaId;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Full name is required")
    private String fullName;

    // Merchant-only fields — all optional at validation level
    // AuthServiceImpl validates them when role == MERCHANT
    private String businessName;
    private String businessLicenseNumber;
    private Double storeGpsLat;
    private Double storeGpsLng;
    private String telebirrAccount;
    private String kebeleCode;
}
