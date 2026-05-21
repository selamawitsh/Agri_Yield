package com.agriyield.farmservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DigitalTwinResponse {

    private String farmId;
    private String geoJsonPolygon;
    private List<NdviReadingResponse> ndviHistory;
    private List<PhotoRecordResponse> photoHistory;
    private List<AgronomistReportResponse> agronomistReports;
    private List<WeatherRecordResponse> weatherLog;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NdviReadingResponse {
        private String date;
        private Double ndvi;
        private Double cloudCoverage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoRecordResponse {
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
    public static class AgronomistReportResponse {
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
    public static class WeatherRecordResponse {
        private String date;
        private Double rainfallMm;
        private Double tempC;
        private Boolean isDryDay;
    }
}
