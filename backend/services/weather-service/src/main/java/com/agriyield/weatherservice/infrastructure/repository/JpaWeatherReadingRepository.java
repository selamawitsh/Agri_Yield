package com.agriyield.weatherservice.infrastructure.repository;

import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity.WeatherReadingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaWeatherReadingRepository extends JpaRepository<WeatherReadingEntity, UUID> {

    List<WeatherReadingEntity> findByFarmIdOrderByRecordedDateDesc(UUID farmId);

    List<WeatherReadingEntity> findByFarmIdAndRecordedDateBetweenOrderByRecordedDateAsc(
            UUID farmId,
            LocalDate from,
            LocalDate to
    );

    Optional<WeatherReadingEntity> findTopByFarmIdAndForecastTypeOrderByFetchedAtDesc(
            UUID farmId,
            String forecastType
    );

    @Query("""
        SELECT w
        FROM WeatherReadingEntity w
        WHERE w.farmId = :farmId
          AND w.forecastType = 'FORECAST'
        ORDER BY w.forecastHorizonDays ASC
    """)
    List<WeatherReadingEntity> findForecastsByFarmId(
            @Param("farmId") UUID farmId
    );

    @Query("""
        SELECT COUNT(w)
        FROM WeatherReadingEntity w
        WHERE w.farmId = :farmId
          AND w.isDryDay = true
          AND w.recordedDate >= :since
          AND w.forecastType = 'ACTUAL'
    """)
    int countDryDaysSince(
            @Param("farmId") UUID farmId,
            @Param("since") LocalDate since
    );
}