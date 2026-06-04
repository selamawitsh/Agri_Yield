package com.agriyield.weatherservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherRisk {

    private double score;
    private double rainfallRisk;
    private double droughtRisk;
    private double ndviVolatility;
}