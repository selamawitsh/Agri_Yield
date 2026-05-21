package com.agriyield.farmservice.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputNeedRequest {

    @NotNull(message = "Crop cycle ID is required")
    private UUID cropCycleId;

    @NotNull(message = "Items list is required")
    @Size(min = 1, message = "At least one input need item is required")
    @Valid
    private List<InputNeedItemRequest> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputNeedItemRequest {

        @NotBlank(message = "Product category is required")
        private String productCategory;

        @NotBlank(message = "Product name is required")
        private String productName;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        @NotBlank(message = "Unit is required")
        private String unit;

        @NotNull(message = "Estimated price is required")
        @DecimalMin(value = "0.01", message = "Estimated price must be greater than 0")
        private BigDecimal estimatedPriceEtb;

        @NotNull(message = "Sequence order is required")
        @Min(value = 1, message = "Sequence order must be at least 1")
        private Integer sequenceOrder;
    }
}
