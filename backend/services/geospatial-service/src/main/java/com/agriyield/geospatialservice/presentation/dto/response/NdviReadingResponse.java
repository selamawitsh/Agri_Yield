package com.agriyield.geospatialservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NdviReadingResponse {
    private UUID farmId;
    private double ndviValue;
    private double cloudCoverage;
    private String healthStatus;
    private String sentinelSceneId;
    private LocalDate recordedDate;
}
