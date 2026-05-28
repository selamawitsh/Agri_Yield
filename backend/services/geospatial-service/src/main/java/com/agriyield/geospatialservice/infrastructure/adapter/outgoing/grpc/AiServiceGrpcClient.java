package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.geospatialservice.application.port.outgoing.AiServicePort;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
public class AiServiceGrpcClient implements AiServicePort {

    // @GrpcClient("ai-service") — wired when ai-service proto is compiled
    // private AiServiceGrpc.AiServiceBlockingStub aiStub;

    @Override
    public YieldPrediction predictYield(UUID farmId,
                                         String cropType,
                                         double ndviPeak,
                                         double ndviGrowthRate,
                                         double ndviCurrent,
                                         double totalRainfallMm,
                                         double avgTempC,
                                         double areaHectares,
                                         int daysSincePlanting) {
        log.info("gRPC: predictYield farmId={} cropType={} (STUB)", farmId, cropType);

        // SRS §3.7.3: stub until ai-service is built.
        // Returns realistic mock predictions based on NDVI health.
        double baseYield   = ndviPeak > 0.6 ? 22.0 : ndviPeak > 0.4 ? 16.0 : 10.0;
        double totalMean   = baseYield * areaHectares;
        double totalMin    = totalMean * 0.85;
        double totalMax    = totalMean * 1.15;
        int confidence     = ndviPeak > 0.6 ? 80 : ndviPeak > 0.4 ? 65 : 50;

        return YieldPrediction.builder()
            .farmId(farmId)
            .cropType(cropType)
            .predictedYieldMin(baseYield * 0.85)
            .predictedYieldMax(baseYield * 1.15)
            .predictedYieldMean(baseYield)
            .totalYieldMinQuintals(totalMin)
            .totalYieldMaxQuintals(totalMax)
            .totalYieldMeanQuintals(totalMean)
            .confidencePct(confidence)
            .weeksToHarvest(6)
            .modelVersion("stub_v1.0")
            .ndviPeak(ndviPeak)
            .ndviGrowthRate(ndviGrowthRate)
            .totalRainfallMm(totalRainfallMm)
            .avgTempC(avgTempC)
            .altitudeM(2100.0)
            .inputQuality("IMPROVED")
            .predictedAt(LocalDateTime.now())
            .createdAt(LocalDateTime.now())
            .build();
    }
}
