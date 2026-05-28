package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "weather_alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "alert_type", nullable = false, length = 30)
    private String alertType;

    @Column(name = "severity", nullable = false, length = 10)
    private String severity;

    @Column(name = "message_en", nullable = false)
    private String messageEn;

    @Column(name = "message_am")
    private String messageAm;

    @Column(name = "message_om")
    private String messageOm;

    @Column(name = "forecast_value", precision = 10, scale = 2)
    private BigDecimal forecastValue;

    @Column(name = "forecast_date")
    private LocalDate forecastDate;

    @Column(name = "is_sent", nullable = false)
    private boolean isSent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
