package com.agriyield.weatherservice.application.port.outgoing;

import com.agriyield.weatherservice.domain.model.WeatherReading;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WeatherReadingRepositoryPort {
    WeatherReading save(WeatherReading reading);
    List<WeatherReading> findByFarmId(UUID farmId);
    List<WeatherReading> findByFarmIdAndDateRange(UUID farmId, LocalDate from, LocalDate to);
    Optional<WeatherReading> findLatestActualByFarmId(UUID farmId);
    List<WeatherReading> findForecastsByFarmId(UUID farmId);
    int countDryDaysSince(UUID farmId, LocalDate since);
}
