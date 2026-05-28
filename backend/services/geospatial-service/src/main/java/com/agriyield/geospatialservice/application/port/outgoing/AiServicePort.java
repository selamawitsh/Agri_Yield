package com.agriyield.geospatialservice.application.port.outgoing;

import com.agriyield.geospatialservice.domain.model.YieldPrediction;

import java.util.UUID;

public interface AiServicePort {

    /**
     * SRS §3.7.3: Call XGBoost yield prediction model via gRPC.
     * Returns null if ai-service is unavailable (graceful degradation).
     */
    YieldPrediction predictYield(UUID farmId,
                                  String cropType,
                                  double ndviPeak,
                                  double ndviGrowthRate,
                                  double ndviCurrent,
                                  double totalRainfallMm,
                                  double avgTempC,
                                  double areaHectares,
                                  int daysSincePlanting);
}
