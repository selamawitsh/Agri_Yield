package com.agriyield.voucherservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedeemVoucherRequest {

    @NotBlank(message = "Voucher code is required")
    private String voucherCode;

    private String notes;
}
