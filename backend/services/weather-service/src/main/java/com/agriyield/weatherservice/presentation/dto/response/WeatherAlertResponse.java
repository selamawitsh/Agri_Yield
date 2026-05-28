package com.agriyield.weatherservice.presentation.dto.response;

import com.agriyield.weatherservice.domain.model.WeatherAlert;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class WeatherAlertResponse {
    private UUID id;
    private UUID farmId;
    private String alertType;
    private String severity;
    private String messageEn;
    private String messageAm;
    private String messageOm;
    private BigDecimal forecastValue;
    private LocalDate forecastDate;
    private OffsetDateTime createdAt;

    public static WeatherAlertResponse from(WeatherAlert domain) {
        return WeatherAlertResponse.builder()
                .id(domain.getId())
                .farmId(domain.getFarmId())
                .alertType(domain.getAlertType().name())
                .severity(domain.getSeverity().name())
                .messageEn(domain.getMessageEn())
                .messageAm(domain.getMessageAm())
                .messageOm(domain.getMessageOm())
                .forecastValue(domain.getForecastValue())
                .forecastDate(domain.getForecastDate())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
