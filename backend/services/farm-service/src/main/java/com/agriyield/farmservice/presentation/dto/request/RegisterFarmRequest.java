package com.agriyield.farmservice.presentation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
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
public class RegisterFarmRequest {

    private String farmName;

    @NotBlank(message = "Crop type is required")
    private String cropType;

    @NotBlank(message = "Kebele code is required")
    private String kebeleCode;

    @NotBlank(message = "Region is required")
    private String region;

    @NotNull(message = "Expected harvest date is required")
    @Future(message = "Expected harvest date must be in the future")
    private LocalDate expectedHarvestDate;

    // GeoJSON Polygon string
    // e.g. {"type":"Polygon","coordinates":[[[38.74,9.03],[38.75,9.03],[38.75,9.04],[38.74,9.03]]]}
    @NotBlank(message = "Farm GPS polygon is required")
    private String geoJsonPolygon;
}
