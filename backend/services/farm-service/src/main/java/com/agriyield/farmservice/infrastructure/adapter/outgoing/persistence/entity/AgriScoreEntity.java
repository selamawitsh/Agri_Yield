package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
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
@Entity
@Table(name = "agri_scores")
public class AgriScoreEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "crop_cycle_id", nullable = false)
    private UUID cropCycleId;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "voucher_discipline_pts", nullable = false)
    private Integer voucherDisciplinePts;

    @Column(name = "yield_accuracy_pts", nullable = false)
    private Integer yieldAccuracyPts;

    @Column(name = "contract_fulfillment_pts", nullable = false)
    private Integer contractFulfillmentPts;

    @Column(name = "repayment_completion_pts", nullable = false)
    private Integer repaymentCompletionPts;

    @Column(name = "season_completion_pts", nullable = false)
    private Integer seasonCompletionPts;

    @Column(name = "agronomist_assessment_pts", nullable = false)
    private Integer agronomistAssessmentPts;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;
}
