package com.agriyield.offtakerservice.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PlaceBidRequest {
    @NotNull
    private UUID farmId;

    @NotNull @DecimalMin("1.0")
    private BigDecimal quantityQuintals;

    @NotNull @DecimalMin("1.0")
    private BigDecimal pricePerQuintalEtb;

    @Min(1) @Max(90)
    private int expiresInDays = 7;
}
