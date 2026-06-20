package com.agriyield.aiservice.application.port.incoming;

import com.agriyield.aiservice.domain.model.AdvisorySession;
import com.agriyield.aiservice.domain.model.CropDiagnosis;
import org.springframework.web.multipart.MultipartFile;

public interface AiServicePort {

    AdvisorySession processVoiceAdvisory(String farmId, String farmerId,
                                          MultipartFile audioFile, String language);

    AdvisorySession processTextAdvisory(String farmId, String farmerId,
                                         String query, String language);

    CropDiagnosis diagnoseCropDisease(String farmId, String farmerId,
                                       String photoUrl, String photoId,
                                       String triggeredBy,
                                       Integer daysPostPlanting,
                                       Double currentNdvi);

    YieldPredictionResult predictYield(YieldPredictionInput input);

    FraudRiskResult scoreFraudRisk(String entityId, String entityType,
                                    String eventType, String eventPayloadJson);

    record YieldPredictionInput(
            String farmId, String cropType,
            double ndviPeak, double ndviGrowthRate,
            double ndviCurrent, double ndviSmoothness,
            double totalRainfallMm, double avgTemperatureC,
            int altitudeM, int cropVarietyEncoded,
            double farmAreaHa, int inputQualityEncoded,
            int daysSincePlanting, double historicalZoneYield,
            String modelVersion
    ) {}

    record YieldPredictionResult(
            double predictedYieldQuintalsPerHa,
            double lowerBound, double upperBound,
            int confidencePct, String modelVersion
    ) {}

    record FraudRiskResult(
            double fraudProbability,
            String modelVersion, String riskLevel
    ) {}
}
