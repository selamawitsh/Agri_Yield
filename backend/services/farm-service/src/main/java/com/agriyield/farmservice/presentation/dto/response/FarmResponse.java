package com.agriyield.farmservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FarmResponse {

    private UUID id;
    private UUID farmerId;
    private String farmName;
    private String cropType;
    private BigDecimal areaHectares;
    private String status;
    private String kebeleCode;
    private String region;
    private BigDecimal gpsCentroidLat;
    private BigDecimal gpsCentroidLng;
    private Boolean satelliteVerified;
    private LocalDateTime satelliteVerifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
