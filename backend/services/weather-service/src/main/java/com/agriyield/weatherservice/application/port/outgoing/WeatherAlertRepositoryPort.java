package com.agriyield.weatherservice.application.port.outgoing;

import com.agriyield.weatherservice.domain.model.WeatherAlert;
import java.util.List;
import java.util.UUID;

public interface WeatherAlertRepositoryPort {
    WeatherAlert save(WeatherAlert alert);
    List<WeatherAlert> findByFarmId(UUID farmId);
    List<WeatherAlert> findUnsentAlerts();
}
