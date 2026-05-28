package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.weatherservice.application.port.outgoing.WeatherAlertRepositoryPort;
import com.agriyield.weatherservice.domain.model.WeatherAlert;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.mapper.WeatherEntityMapper;
import com.agriyield.weatherservice.infrastructure.repository.JpaWeatherAlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WeatherAlertRepositoryAdapter implements WeatherAlertRepositoryPort {

    private final JpaWeatherAlertRepository jpaRepository;
    private final WeatherEntityMapper mapper;

    @Override
    public WeatherAlert save(WeatherAlert alert) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(alert)));
    }

    @Override
    public List<WeatherAlert> findByFarmId(UUID farmId) {
        return jpaRepository.findByFarmIdOrderByCreatedAtDesc(farmId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<WeatherAlert> findUnsentAlerts() {
        return jpaRepository.findByIsSentFalse()
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
