package com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OtpVerifyRequest {
    
    @NotBlank(message = "Phone number is required")
    private String phone;
    
    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
    private String otpCode;
    
    @NotBlank(message = "Purpose is required")
    @Pattern(regexp = "REGISTRATION|LOGIN|PASSWORD_RESET|BANK_VERIFY", message = "Invalid purpose")
    private String purpose;
}
