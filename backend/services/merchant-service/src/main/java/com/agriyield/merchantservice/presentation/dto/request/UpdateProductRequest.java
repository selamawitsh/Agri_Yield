package com.agriyield.merchantservice.presentation.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {
    private String productName;
    private BigDecimal currentPriceEtb;
    private Boolean isAvailable;
}
