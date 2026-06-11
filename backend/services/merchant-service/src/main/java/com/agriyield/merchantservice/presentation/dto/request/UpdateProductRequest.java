package com.agriyield.merchantservice.presentation.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String productName;
    private BigDecimal currentPriceEtb;
    private Boolean isAvailable;

    /** Update stock quantity — use to restock after delivery. */
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private BigDecimal quantityInStock;
}
