package com.agriyield.farmservice.infrastructure.adapter.outgoing.mongodb.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "farm_documents")
public class FarmDocumentEntity {

    @Id
    private String id;

    @Field("farmId")
    private String farmId;

    @Field("geoJsonPolygon")
    private String geoJsonPolygon;

    @Builder.Default
    @Field("ndviHistory")
    private List<NdviReadingDoc> ndviHistory = new ArrayList<>();

    @Builder.Default
    @Field("photoHistory")
    private List<PhotoRecordDoc> photoHistory = new ArrayList<>();

    @Builder.Default
    @Field("agronomistReports")
    private List<AgronomistReportDoc> agronomistReports = new ArrayList<>();

    @Builder.Default
    @Field("weatherLog")
    private List<WeatherRecordDoc> weatherLog = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NdviReadingDoc {
        private String date;
        private Double ndvi;
        private Double cloudCoverage;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhotoRecordDoc {
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
    public static class AgronomistReportDoc {
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
    public static class WeatherRecordDoc {
        private String date;
        private Double rainfallMm;
        private Double tempC;
        private Boolean isDryDay;
    }
}
