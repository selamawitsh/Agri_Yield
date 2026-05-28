package com.agriyield.weatherservice.domain.model;

import com.agriyield.weatherservice.domain.enums.AlertSeverity;
import com.agriyield.weatherservice.domain.enums.AlertType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlert {
    private UUID id;
    private UUID farmId;
    private AlertType alertType;
    private AlertSeverity severity;
    private String messageEn;
    private String messageAm;
    private String messageOm;
    private BigDecimal forecastValue;
    private LocalDate forecastDate;
    private boolean isSent;
    private OffsetDateTime createdAt;
}
