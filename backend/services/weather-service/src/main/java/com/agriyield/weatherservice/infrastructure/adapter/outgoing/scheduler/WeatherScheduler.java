package com.agriyield.weatherservice.infrastructure.adapter.outgoing.scheduler;

import com.agriyield.weatherservice.application.port.incoming.WeatherServicePort;
import com.agriyield.weatherservice.application.port.outgoing.FarmServiceClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherScheduler {

    private final WeatherServicePort weatherService;
    private final FarmServiceClientPort farmServiceClient;

    /**
     * WS-09: Fetch current weather for all active farms every 6 hours
     */
    @Scheduled(cron = "0 0 */6 * * *", zone = "UTC")
    public void fetchWeatherForAllFarms() {
        log.info("Scheduled: Fetching weather for all active farms");
        List<UUID> farmIds = farmServiceClient.getActiveFarmIds();
        log.info("Found {} active farms", farmIds.size());

        for (UUID farmId : farmIds) {
            try {
                weatherService.fetchAndStoreWeather(farmId);
            } catch (Exception e) {
                log.error("Failed to fetch weather for farm {}: {}", farmId, e.getMessage());
            }
        }
        log.info("Weather fetch completed for {} farms", farmIds.size());
    }

    /**
     * WS-03 / WS-04: Run drought analysis daily at midnight UTC
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void runDroughtAnalysis() {
        log.info("Scheduled: Running drought analysis for all active farms");
        List<UUID> farmIds = farmServiceClient.getActiveFarmIds();

        for (UUID farmId : farmIds) {
            try {
                weatherService.analyzeDrought(farmId);
            } catch (Exception e) {
                log.error("Drought analysis failed for farm {}: {}", farmId, e.getMessage());
            }
        }
        log.info("Drought analysis completed");
    }
}
