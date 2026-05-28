package com.agriyield.weatherservice.domain.model;

import com.agriyield.weatherservice.domain.enums.ForecastType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReading {
    private UUID id;
    private UUID farmId;
    private BigDecimal gpsLat;
    private BigDecimal gpsLng;
    private BigDecimal temperatureC;
    private BigDecimal rainfallMm;
    private BigDecimal humidityPct;
    private boolean isDryDay;
    private ForecastType forecastType;
    private Integer forecastHorizonDays;
    private LocalDate recordedDate;
    private OffsetDateTime fetchedAt;
}
