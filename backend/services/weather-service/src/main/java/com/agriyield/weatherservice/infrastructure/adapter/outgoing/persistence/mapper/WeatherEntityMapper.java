package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.weatherservice.domain.enums.AlertSeverity;
import com.agriyield.weatherservice.domain.enums.AlertType;
import com.agriyield.weatherservice.domain.enums.ForecastType;
import com.agriyield.weatherservice.domain.model.DroughtCondition;
import com.agriyield.weatherservice.domain.model.WeatherAlert;
import com.agriyield.weatherservice.domain.model.WeatherReading;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity.DroughtConditionEntity;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity.WeatherAlertEntity;
import com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity.WeatherReadingEntity;
import org.springframework.stereotype.Component;

@Component
public class WeatherEntityMapper {

    public WeatherReading toDomain(WeatherReadingEntity e) {
        if (e == null) return null;
        return WeatherReading.builder()
                .id(e.getId())
                .farmId(e.getFarmId())
                .gpsLat(e.getGpsLat())
                .gpsLng(e.getGpsLng())
                .temperatureC(e.getTemperatureC())
                .rainfallMm(e.getRainfallMm())
                .humidityPct(e.getHumidityPct())
                .isDryDay(Boolean.TRUE.equals(e.getIsDryDay()))
                .forecastType(ForecastType.valueOf(e.getForecastType()))
                .forecastHorizonDays(e.getForecastHorizonDays())
                .recordedDate(e.getRecordedDate())
                .fetchedAt(e.getFetchedAt())
                .build();
    }

    public WeatherReadingEntity toEntity(WeatherReading d) {
        if (d == null) return null;
        return WeatherReadingEntity.builder()
                .id(d.getId())
                .farmId(d.getFarmId())
                .gpsLat(d.getGpsLat())
                .gpsLng(d.getGpsLng())
                .temperatureC(d.getTemperatureC())
                .rainfallMm(d.getRainfallMm())
                .humidityPct(d.getHumidityPct())
                .forecastType(d.getForecastType().name())
                .forecastHorizonDays(d.getForecastHorizonDays())
                .recordedDate(d.getRecordedDate())
                .build();
    }

    public DroughtCondition toDomain(DroughtConditionEntity e) {
        if (e == null) return null;
        return DroughtCondition.builder()
                .id(e.getId())
                .farmId(e.getFarmId())
                .consecutiveDryDays(e.getConsecutiveDryDays())
                .droughtThresholdDays(e.getDroughtThresholdDays())
                .isTriggered(e.isTriggered())
                .triggeredAt(e.getTriggeredAt())
                .lastChecked(e.getLastChecked())
                .build();
    }

    public DroughtConditionEntity toEntity(DroughtCondition d) {
        return DroughtConditionEntity.builder()
                .id(d.getId())
                .farmId(d.getFarmId())
                .consecutiveDryDays(d.getConsecutiveDryDays())
                .droughtThresholdDays(d.getDroughtThresholdDays())
                .isTriggered(d.isTriggered())
                .triggeredAt(d.getTriggeredAt())
                .lastChecked(d.getLastChecked())
                .build();
    }

    public WeatherAlert toDomain(WeatherAlertEntity e) {
        if (e == null) return null;
        return WeatherAlert.builder()
                .id(e.getId())
                .farmId(e.getFarmId())
                .alertType(AlertType.valueOf(e.getAlertType()))
                .severity(AlertSeverity.valueOf(e.getSeverity()))
                .messageEn(e.getMessageEn())
                .messageAm(e.getMessageAm())
                .messageOm(e.getMessageOm())
                .forecastValue(e.getForecastValue())
                .forecastDate(e.getForecastDate())
                .isSent(e.isSent())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public WeatherAlertEntity toEntity(WeatherAlert d) {
        return WeatherAlertEntity.builder()
                .id(d.getId())
                .farmId(d.getFarmId())
                .alertType(d.getAlertType().name())
                .severity(d.getSeverity().name())
                .messageEn(d.getMessageEn())
                .messageAm(d.getMessageAm())
                .messageOm(d.getMessageOm())
                .forecastValue(d.getForecastValue())
                .forecastDate(d.getForecastDate())
                .isSent(d.isSent())
                .build();
    }
}
