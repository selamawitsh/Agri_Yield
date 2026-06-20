package com.agriyield.aiservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CropDiagnosis {

    private String id;
    private String farmId;
    private String farmerId;
    private String photoId;
    private String photoUrl;
    private String cropType;
    private Double currentNdvi;
    private Integer daysPostPlanting;

    // Gemini Vision output fields (from SRS)
    private String diseaseName;
    private Integer confidencePct;
    private String symptomsObserved;
    private String recommendedTreatment;
    private String severity;           // LOW, MEDIUM, HIGH
    private Boolean escalateToAgronomist;

    private String triggeredBy;        // MANUAL or EVENT (crop.photo.uploaded)
    private LocalDateTime diagnosedAt;
}