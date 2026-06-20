package com.agriyield.geospatialservice.application.port.outgoing;

/**
 * Outgoing port in geospatial-service for calling ai-service yield prediction.
 */
public interface AiServicePort {

    YieldPrediction predictYield(YieldPredictionInput input);

    record YieldPredictionInput(
            String farmId,
            String cropType,
            double ndviPeak,
            double ndviGrowthRate,
            double ndviCurrent,
            double ndviSmoothness,
            double totalRainfallMm,
            double avgTemperatureC,
            int altitudeM,
            int cropVarietyEncoded,
            double farmAreaHa,
            int inputQualityEncoded,
            int daysSincePlanting,
            double historicalZoneYield
    ) {}

    record YieldPrediction(
            double predictedYieldQuintalsPerHa,
            double lowerBound,
            double upperBound,
            int confidencePct,
            String modelVersion
    ) {}
}