package com.agriyield.investmentservice.presentation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInvestmentRequest {

    @NotNull(message = "Farm ID is required")
    private UUID farmId;

    @NotNull(message = "Input need ID is required")
    private UUID inputNeedId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "500.00", message = "Minimum investment is 500 ETB")
    private BigDecimal amountEtb;

    private String notes;
}
