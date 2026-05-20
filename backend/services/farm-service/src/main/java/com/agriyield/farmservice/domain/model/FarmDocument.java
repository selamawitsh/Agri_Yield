package com.agriyield.farmservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// SRS Page 21-22 — MongoDB digital twin document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmDocument {

    private String farmId;
    private String geoJsonPolygon;

    @Builder.Default
    private List<NdviReading> ndviHistory = new ArrayList<>();

    @Builder.Default
    private List<PhotoRecord> photoHistory = new ArrayList<>();

    @Builder.Default
    private List<AgronomistReport> agronomistReports = new ArrayList<>();

    @Builder.Default
    private List<WeatherRecord> weatherLog = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NdviReading {
        private String date;
        private Double ndvi;
        private Double cloudCoverage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoRecord {
        private String photoId;
        private String type;
        private String url;
        private Double lat;
        private Double lng;
        private String uploadedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgronomistReport {
        private String reportId;
        private String agronomistId;
        private String visitDate;
        private String diagnosis;
        private String treatment;
        private Integer rating;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherRecord {
        private String date;
        private Double rainfallMm;
        private Double tempC;
        private Boolean isDryDay;
    }
}
