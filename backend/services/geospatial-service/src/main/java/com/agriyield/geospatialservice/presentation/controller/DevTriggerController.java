package com.agriyield.geospatialservice.presentation.controller;

import com.agriyield.geospatialservice.application.port.incoming.GeospatialServicePort;
import com.agriyield.geospatialservice.presentation.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TEMPORARY DEV-ONLY CONTROLLER.
 * Lets you manually fire the scheduled jobs from SchedulerConfig on demand,
 * instead of waiting days for cron (every 5 days / weekly / every 2 days).
 *
 * DELETE THIS FILE before deploying to any real environment — it has no
 * auth guard and triggers expensive operations (satellite API calls, ML
 * model calls) on every farm in the system.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dev/geospatial")
@RequiredArgsConstructor
public class DevTriggerController {

    private final GeospatialServicePort geospatialService;

    @PostMapping("/trigger/ndvi-sync")
    public ResponseEntity<ApiResponse<String>> triggerNdviSync() {
        log.warn("DEV TRIGGER: manually firing ndviSyncAllActiveFarms()");
        geospatialService.syncNdviAllActiveFarms();
        return ResponseEntity.ok(ApiResponse.success("NDVI sync triggered for all active farms"));
    }

    @PostMapping("/trigger/yield-predictions")
    public ResponseEntity<ApiResponse<String>> triggerYieldPredictions() {
        log.warn("DEV TRIGGER: manually firing runWeeklyYieldPredictions()");
        geospatialService.runWeeklyYieldPredictions();
        return ResponseEntity.ok(ApiResponse.success("Yield predictions triggered"));
    }

    @PostMapping("/trigger/harvest-readiness")
    public ResponseEntity<ApiResponse<String>> triggerHarvestReadiness() {
        log.warn("DEV TRIGGER: manually firing runHarvestReadinessDetection()");
        geospatialService.runHarvestReadinessDetection();
        return ResponseEntity.ok(ApiResponse.success("Harvest readiness detection triggered"));
    }
}
