package com.agriyield.fraudservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FraudRiskScoreResponse {

    private UUID entityId;
    private String entityType;
    private int gpsAnomalyScore;
    private int duplicateVoucherScore;
    private int exifMismatchScore;
    private int suspiciousActivityScore;
    private int totalScore;
    private String severity;
    private LocalDateTime calculatedAt;
}
