package com.agriyield.farmservice.domain.model;

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
public class AgriScore {

    private UUID id;
    private UUID farmerId;
    private UUID cropCycleId;

    private Integer score;

    // SRS Page 21 — 6 components
    private Integer voucherDisciplinePts;    // max 150
    private Integer yieldAccuracyPts;        // max 200
    private Integer contractFulfillmentPts;  // max 200
    private Integer repaymentCompletionPts;  // max 200
    private Integer seasonCompletionPts;     // flat 100
    private Integer agronomistAssessmentPts; // max 50

    private LocalDateTime calculatedAt;

    // SRS Page 23 — Agri-Score formula
    public static int calculate(
            int voucherDisciplinePts,
            int yieldAccuracyPts,
            int contractFulfillmentPts,
            int repaymentCompletionPts,
            int seasonCompletionPts,
            int agronomistAssessmentPts) {

        int score = voucherDisciplinePts
                + yieldAccuracyPts
                + contractFulfillmentPts
                + repaymentCompletionPts
                + seasonCompletionPts
                + agronomistAssessmentPts;

        return Math.min(score, 900);
    }
}
