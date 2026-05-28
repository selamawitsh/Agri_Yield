package com.agriyield.geospatialservice.infrastructure.config;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulerConfig {

    private final GeospatialServicePort geospatialService;

    /**
     * SRS 3.6.3:
     * NDVI sync for all farms
     * Runs every 5 days at 06:00 UTC
     *
     * Spring cron format:
     * second minute hour day month weekday
     */
    @Scheduled(cron = "${app.ndvi.sync-cron:0 0 6 */5 * *}")
    public void ndviSyncAllFarms() {
        log.info("SCHEDULER: ndviSyncAllFarms triggered");
        geospatialService.syncNdviAllActiveFarms();
    }

    /**
     * SRS 3.6.3:
     * Weekly yield prediction
     * Runs every Monday at 08:00 UTC
     */
    @Scheduled(cron = "${app.yield.prediction-cron:0 0 8 * * MON}")
    public void weeklyYieldPredictions() {
        log.info("SCHEDULER: weeklyYieldPredictions triggered");
        geospatialService.runWeeklyYieldPredictions();
    }

    /**
     * SRS 3.6.3:
     * Harvest readiness detection
     * Runs every 2 days at 09:00 UTC
     */
    @Scheduled(cron = "${app.yield.harvest-detection-cron:0 0 9 */2 * *}")
    public void harvestReadinessDetection() {
        log.info("SCHEDULER: harvestReadinessDetection triggered");
        geospatialService.runHarvestReadinessDetection();
    }
}