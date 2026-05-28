package com.agriyield.weatherservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "weather_readings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "farm_id", nullable = false)
    private UUID farmId;

    @Column(name = "gps_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal gpsLat;

    @Column(name = "gps_lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal gpsLng;

    @Column(name = "temperature_c", nullable = false, precision = 5, scale = 2)
    private BigDecimal temperatureC;

    @Column(name = "rainfall_mm", nullable = false, precision = 8, scale = 2)
    private BigDecimal rainfallMm;

    @Column(name = "humidity_pct", precision = 5, scale = 2)
    private BigDecimal humidityPct;

    @Column(name = "is_dry_day", insertable = false, updatable = false)
    private Boolean isDryDay;

    @Column(name = "forecast_type", nullable = false, length = 10)
    private String forecastType;

    @Column(name = "forecast_horizon_days")
    private Integer forecastHorizonDays;

    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate;

    @CreationTimestamp
    @Column(name = "fetched_at", nullable = false)
    private OffsetDateTime fetchedAt;
}
