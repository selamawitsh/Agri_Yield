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
public class FarmPhotoResponse {

    private UUID id;
    private UUID farmId;
    private String photoUrl;
    private BigDecimal gpsLat;
    private BigDecimal gpsLng;
    private String photoType;
    private Boolean gpsVerified;
    private LocalDateTime uploadedAt;
}
