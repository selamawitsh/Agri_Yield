package com.agriyield.farmservice.presentation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCropCycleRequest {

    // Optional — auto-generated if not provided
    private String seasonName;

    @NotNull(message = "Expected harvest date is required")
    @Future(message = "Expected harvest date must be in the future")
    private LocalDate expectedHarvestDate;
}
