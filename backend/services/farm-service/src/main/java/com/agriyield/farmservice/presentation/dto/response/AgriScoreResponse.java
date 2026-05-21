package com.agriyield.farmservice.presentation.dto.response;

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
public class AgriScoreResponse {

    private UUID id;
    private UUID farmerId;
    private UUID cropCycleId;
    private Integer score;

    // SRS Page 21 — 6 component breakdown
    private Integer voucherDisciplinePts;
    private Integer yieldAccuracyPts;
    private Integer contractFulfillmentPts;
    private Integer repaymentCompletionPts;
    private Integer seasonCompletionPts;
    private Integer agronomistAssessmentPts;

    private LocalDateTime calculatedAt;
}
