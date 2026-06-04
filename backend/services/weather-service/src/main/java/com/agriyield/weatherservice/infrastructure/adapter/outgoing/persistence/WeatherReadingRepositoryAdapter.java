package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.weatherservice.application.port.outgoing.WeatherReadingRepositoryPort;
import com.agriyield.weatherservice.domain.model.WeatherReading;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.mapper.WeatherEntityMapper;
import com.agriyield.weatherservice.infrastructure.repository.JpaWeatherReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WeatherReadingRepositoryAdapter implements WeatherReadingRepositoryPort {

    private final JpaWeatherReadingRepository jpaRepository;
    private final WeatherEntityMapper mapper;

    @Override
    public WeatherReading save(WeatherReading reading) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(reading))
        );
    }

    @Override
    public List<WeatherReading> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmIdOrderByRecordedDateDesc(farmId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WeatherReading> findByFarmIdAndDateRange(
            UUID farmId,
            LocalDate from,
            LocalDate to
    ) {
        return jpaRepository
                .findByFarmIdAndRecordedDateBetweenOrderByRecordedDateAsc(
                        farmId,
                        from,
                        to
                )
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WeatherReading> findLatestActualByFarmId(UUID farmId) {
        return jpaRepository
                .findTopByFarmIdAndForecastTypeOrderByFetchedAtDesc(
                        farmId,
                        "ACTUAL"
                )
                .map(mapper::toDomain);
    }

    @Override
    public List<WeatherReading> findForecastsByFarmId(UUID farmId) {
        return jpaRepository.findForecastsByFarmId(farmId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countDryDaysSince(UUID farmId, LocalDate since) {
        return jpaRepository.countDryDaysSince(farmId, since);
    }
}