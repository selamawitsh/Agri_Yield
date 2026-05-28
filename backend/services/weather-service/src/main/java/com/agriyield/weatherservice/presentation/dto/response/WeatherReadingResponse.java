package com.agriyield.weatherservice.presentation.dto.response;

import com.agriyield.weatherservice.domain.model.WeatherReading;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class WeatherReadingResponse {
    private UUID id;
    private UUID farmId;
    private BigDecimal temperatureC;
    private BigDecimal rainfallMm;
    private BigDecimal humidityPct;
    private boolean isDryDay;
    private String forecastType;
    private Integer forecastHorizonDays;
    private LocalDate recordedDate;

    public static WeatherReadingResponse from(WeatherReading domain) {
        return WeatherReadingResponse.builder()
                .id(domain.getId())
                .farmId(domain.getFarmId())
                .temperatureC(domain.getTemperatureC())
                .rainfallMm(domain.getRainfallMm())
                .humidityPct(domain.getHumidityPct())
                .isDryDay(domain.isDryDay())
                .forecastType(domain.getForecastType().name())
                .forecastHorizonDays(domain.getForecastHorizonDays())
                .recordedDate(domain.getRecordedDate())
                .build();
    }
}
