package com.agriyield.merchantservice.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Product category is required")
    private String productCategory;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal currentPriceEtb;

    /** Initial stock quantity. Defaults to 0 if not provided. */
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private BigDecimal quantityInStock;
}
