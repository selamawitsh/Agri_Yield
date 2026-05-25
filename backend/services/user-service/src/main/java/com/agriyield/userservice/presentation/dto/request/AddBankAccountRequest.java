package com.agriyield.userservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddBankAccountRequest {

    @NotBlank(message = "Account type is required (TELEBIRR or CBE)")
    private String accountType;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private String accountHolderName;
}
