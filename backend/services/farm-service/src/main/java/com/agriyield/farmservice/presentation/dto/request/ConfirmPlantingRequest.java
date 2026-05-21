package com.agriyield.farmservice.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPlantingRequest {

    @NotNull(message = "Planting date is required")
    @PastOrPresent(message = "Planting date cannot be in the future")
    private LocalDate plantingDate;
}
