package com.agriyield.weatherservice.application.service;

import com.agriyield.weatherservice.application.port.incoming.WeatherServicePort;
import com.agriyield.weatherservice.application.port.outgoing.*;
import com.agriyield.weatherservice.domain.enums.AlertSeverity;
import com.agriyield.weatherservice.domain.enums.AlertType;
import com.agriyield.weatherservice.domain.enums.ForecastType;
import com.agriyield.weatherservice.domain.exception.BusinessException;
import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.domain.model.WeatherAlert;
import com.agriyield.weatherservice.domain.model.WeatherReading;
import com.agriyield.weatherservice.domain.model.WeatherRisk;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WeatherServiceImpl implements WeatherServicePort {

    private final WeatherReadingRepositoryPort weatherReadingRepository;
    private final DroughtConditionRepositoryPort droughtConditionRepository;
    private final WeatherAlertRepositoryPort weatherAlertRepository;
    private final OpenWeatherClientPort openWeatherClient;
    private final EventPublisherPort eventPublisher;
    private final FarmServiceClientPort farmServiceClient;

    @Value("${app.drought.threshold-days:30}")
    private int droughtThresholdDays;

    @Value("${app.drought.warning-days:20}")
    private int droughtWarningDays;

    // WS-01
    @Override
    public List<WeatherReading> getForecast(UUID farmId, int days) {
        log.info("Getting {}-day forecast for farm: {}", days, farmId);
        List<WeatherReading> cached = weatherReadingRepository.findForecastsByFarmId(farmId);
        if (!cached.isEmpty()) return cached;
        double[] coords = farmServiceClient.getFarmCoordinates(farmId);
        List<WeatherReading> forecasts = openWeatherClient.fetchForecast(coords[0], coords[1], days);
        forecasts.forEach(f -> {
            f.setFarmId(farmId);
            weatherReadingRepository.save(f);
        });
        return forecasts;
    }

    // WS-02
    @Override
    public List<WeatherReading> getRainfallData(UUID farmId) {
        log.info("Getting rainfall data for farm: {}", farmId);
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return weatherReadingRepository.findByFarmIdAndDateRange(
                farmId, thirtyDaysAgo, LocalDate.now());
    }

    // WS-03
    @Override
    public DroughtCondition getDroughtStatus(UUID farmId) {
        return droughtConditionRepository.findOrCreateByFarmId(farmId);
    }

    // WS-05
    @Override
    public WeatherReading getCurrentWeather(UUID farmId) {
        return weatherReadingRepository.findLatestActualByFarmId(farmId)
                .orElseThrow(() -> new BusinessException(
                        "No weather data for farm: " + farmId, "WEATHER_NOT_FOUND"));
    }

    // WS-07
    @Override
    public WeatherRisk calculateWeatherRiskScore(UUID farmId) {

        log.info("Calculating weather risk score for farm: {}", farmId);

        DroughtCondition drought = droughtConditionRepository.findOrCreateByFarmId(farmId);

        // Rainfall risk (0 - 40)
        int dryDaysLast30 = weatherReadingRepository.countDryDaysSince(
                farmId, LocalDate.now().minusDays(30));

        double rainfallRisk = Math.min(40.0, dryDaysLast30 * 1.33);

        // Drought risk (0 - 40)
        double droughtRisk = drought.isTriggered()
                ? 40.0
                : (drought.getConsecutiveDryDays() / (double) droughtThresholdDays) * 40.0;

        // NDVI volatility (0 - 20)
        double ndviVolatility = 10.0;

        double total = rainfallRisk + droughtRisk + ndviVolatility;
        double score = Math.min(100.0, total);

        log.info("Weather risk score for farm {}: {}", farmId, score);

        return WeatherRisk.builder()
                .rainfallRisk(rainfallRisk)
                .droughtRisk(droughtRisk)
                .ndviVolatility(ndviVolatility)
                .score(score)
                .build();
    }
    // WS-08
    @Override
    public List<WeatherReading> getHistoricalWeather(UUID farmId) {
        log.info("Getting historical weather for farm: {}", farmId);
        LocalDate ninetyDaysAgo = LocalDate.now().minusDays(90);
        return weatherReadingRepository.findByFarmIdAndDateRange(
                farmId, ninetyDaysAgo, LocalDate.now());
    }

    // WS-09
    @Override
    public List<WeatherAlert> getWeatherAlerts(UUID farmId) {
        return weatherAlertRepository.findByFarmId(farmId);
    }

    @Override
    @Transactional
    public void fetchAndStoreWeather(UUID farmId) {

        try {

            double[] coords =
                    farmServiceClient.getFarmCoordinates(farmId);

            double lat = coords[0];
            double lng = coords[1];

            log.info(
                    "Fetching weather for farm {} at [{}, {}]",
                    farmId,
                    lat,
                    lng
            );

            WeatherReading current =
                    openWeatherClient.fetchCurrentWeather(lat, lng);

            current.setFarmId(farmId);
            current.setForecastType(ForecastType.ACTUAL);

            weatherReadingRepository.save(current);

            checkAndCreateAlerts(farmId, current);

        } catch (Exception e) {

            log.error(
                    "Failed to fetch weather for farm {}",
                    farmId,
                    e
            );

            throw e;
        }
    }

    // WS-03 / WS-04
    @Override
    @Transactional
    public void analyzeDrought(UUID farmId) {
        log.info("Analyzing drought for farm: {}", farmId);
        DroughtCondition condition = droughtConditionRepository.findOrCreateByFarmId(farmId);

        WeatherReading latest = weatherReadingRepository
                .findLatestActualByFarmId(farmId).orElse(null);

        if (latest == null) return;

        if (latest.isDryDay()) {
            condition.incrementDryDay();
        } else {
            condition.resetDryDays();
        }

        droughtConditionRepository.save(condition);

        // WS-03 warning
        if (condition.isWarningLevel(droughtWarningDays)) {
            WeatherAlert alert = buildAlert(farmId, AlertType.DROUGHT_WARNING,
                    AlertSeverity.MEDIUM,
                    condition.getConsecutiveDryDays() + " dry days recorded. Irrigation recommended.",
                    BigDecimal.valueOf(condition.getConsecutiveDryDays()));
            weatherAlertRepository.save(alert);
            eventPublisher.publishWeatherAlert(farmId, alert);
        }

        // WS-04 drought trigger
        if (condition.isTriggered() && condition.getTriggeredAt() != null
                && condition.getTriggeredAt().isAfter(OffsetDateTime.now().minusHours(1))) {
            log.warn("Drought TRIGGERED for farm: {}", farmId);
            eventPublisher.publishDroughtTriggered(farmId, condition);
        }
    }

    private void checkAndCreateAlerts(UUID farmId, WeatherReading reading) {
        // Frost warning: temp < 2°C
        if (reading.getTemperatureC().compareTo(BigDecimal.valueOf(2.0)) < 0
                && reading.getForecastType() == ForecastType.FORECAST) {
            WeatherAlert alert = buildAlert(farmId, AlertType.FROST_WARNING,
                    AlertSeverity.HIGH, "Frost risk detected. Protect seedlings.",
                    reading.getTemperatureC());
            weatherAlertRepository.save(alert);
            eventPublisher.publishWeatherAlert(farmId, alert);
        }

        // Heavy rain: > 50mm in 24h
        if (reading.getRainfallMm().compareTo(BigDecimal.valueOf(50.0)) > 0) {
            WeatherAlert alert = buildAlert(farmId, AlertType.HEAVY_RAIN,
                    AlertSeverity.HIGH,
                    "Heavy rain expected (" + reading.getRainfallMm() + "mm). Avoid fertilizer.",
                    reading.getRainfallMm());
            weatherAlertRepository.save(alert);
            eventPublisher.publishWeatherAlert(farmId, alert);
        }

        // Heatwave: temp > 35°C
        if (reading.getTemperatureC().compareTo(BigDecimal.valueOf(35.0)) > 0) {
            WeatherAlert alert = buildAlert(farmId, AlertType.HEATWAVE,
                    AlertSeverity.MEDIUM, "Heatwave conditions. Monitor soil moisture.",
                    reading.getTemperatureC());
            weatherAlertRepository.save(alert);
            eventPublisher.publishWeatherAlert(farmId, alert);
        }
    }

    private WeatherAlert buildAlert(UUID farmId, AlertType type,
            AlertSeverity severity, String messageEn, BigDecimal value) {
        return WeatherAlert.builder()
                .id(UUID.randomUUID())
                .farmId(farmId)
                .alertType(type)
                .severity(severity)
                .messageEn(messageEn)
                .messageAm("AGRI-YIELD ማስጠንቀቂያ: " + messageEn)
                .messageOm("AGRI-YIELD BEEKSISA: " + messageEn)
                .forecastValue(value)
                .forecastDate(LocalDate.now())
                .isSent(false)
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
