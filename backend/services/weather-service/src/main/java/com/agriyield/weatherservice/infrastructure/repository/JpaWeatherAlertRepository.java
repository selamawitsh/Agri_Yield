package com.agriyield.weatherservice.infrastructure.repository;

import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity.WeatherAlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaWeatherAlertRepository extends JpaRepository<WeatherAlertEntity, UUID> {
    List<WeatherAlertEntity> findByFarmIdOrderByCreatedAtDesc(UUID farmId);
    List<WeatherAlertEntity> findByIsSentFalse();
}
