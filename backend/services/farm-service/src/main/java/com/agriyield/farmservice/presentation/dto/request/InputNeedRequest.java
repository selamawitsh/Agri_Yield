package com.agriyield.farmservice.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputNeedRequest {

    // cropCycleId removed — auto-fetched from active crop cycle
    // Farmers should never need to enter an ID

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
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        private BigDecimal estimatedPriceEtb;

        @NotNull(message = "Sequence order is required")
        @Min(value = 1, message = "Sequence order must be at least 1")
        private Integer sequenceOrder;
    }
}
