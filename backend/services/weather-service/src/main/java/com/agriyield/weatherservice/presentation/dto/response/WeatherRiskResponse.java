package com.agriyield.weatherservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class WeatherRiskResponse {
    private UUID farmId;
    private double riskScore;       // 0-100
    private String riskLevel;       // LOW / MEDIUM / HIGH / CRITICAL
    private double rainfallRisk;
    private double droughtRisk;
    private double ndviVolatility;

    public static String toRiskLevel(double score) {
        if (score < 25) return "LOW";
        if (score < 50) return "MEDIUM";
        if (score < 75) return "HIGH";
        return "CRITICAL";
    }
}
