package com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+251[0-9]{9}$", message = "Phone must be in Ethiopian format: +251XXXXXXXXX")
    private String phone;
    
    @NotBlank(message = "Fayda ID is required")
    @Size(min = 10, max = 50, message = "Fayda ID must be between 10 and 50 characters")
    private String faydaId;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "FARMER|INVESTOR|MERCHANT|OFF_TAKER", message = "Invalid role")
    private String role;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
}
