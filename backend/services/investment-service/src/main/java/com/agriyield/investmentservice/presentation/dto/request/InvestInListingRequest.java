package com.agriyield.investmentservice.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestInListingRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "500.00", message = "Minimum investment is 500 ETB")
    private BigDecimal amountEtb;

    private String notes;
}
