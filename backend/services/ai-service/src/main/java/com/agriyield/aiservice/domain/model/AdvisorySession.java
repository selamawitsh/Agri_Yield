package com.agriyield.aiservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvisorySession {

    private String id;
    private String farmId;
    private String farmerId;
    private String queryType;        // VOICE or TEXT
    private String language;         // am, om, ti, en
    private String originalQuery;    // transcribed text or original text input
    private String audioInputUrl;    // MinIO URL of uploaded audio (nullable)
    private String advisoryText;     // Gemini's response in text
    private String audioResponseUrl; // MinIO URL of TTS-generated audio (nullable)
    private String cropType;
    private Double currentNdvi;
    private String weatherSummary;
    private Integer daysPostPlanting;
    private LocalDateTime createdAt;
}