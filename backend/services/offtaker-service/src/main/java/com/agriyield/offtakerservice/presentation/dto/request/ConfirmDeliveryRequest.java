package com.agriyield.offtakerservice.presentation.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfirmDeliveryRequest {
    @NotNull @DecimalMin("0.01")
    private BigDecimal actualQuantityQuintals;

    @NotBlank
    private String qualityGrade;
}
