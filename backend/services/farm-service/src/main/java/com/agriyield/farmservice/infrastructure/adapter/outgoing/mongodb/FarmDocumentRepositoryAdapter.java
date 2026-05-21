package com.agriyield.farmservice.infrastructure.adapter.outgoing.mongodb;

import com.agriyield.farmservice.application.port.outgoing.FarmDocumentRepositoryPort;
import com.agriyield.farmservice.domain.model.FarmDocument;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.mongodb.document.FarmDocumentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FarmDocumentRepositoryAdapter implements FarmDocumentRepositoryPort {

    private final MongoFarmDocumentRepository mongoRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public FarmDocument save(FarmDocument domain) {
        FarmDocumentEntity entity = toEntity(domain);
        FarmDocumentEntity saved = mongoRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<FarmDocument> findByFarmId(String farmId) {
        return mongoRepository.findByFarmId(farmId).map(this::toDomain);
    }

    @Override
    public void appendNdviReading(String farmId, FarmDocument.NdviReading reading) {
        Query query = Query.query(Criteria.where("farmId").is(farmId));
        Update update = new Update().push("ndviHistory",
            FarmDocumentEntity.NdviReadingDoc.builder()
                .date(reading.getDate())
                .ndvi(reading.getNdvi())
                .cloudCoverage(reading.getCloudCoverage())
                .build());
        mongoTemplate.updateFirst(query, update, FarmDocumentEntity.class);
        log.info("Appended NDVI reading for farm: {}", farmId);
    }

    @Override
    public void appendPhotoRecord(String farmId, FarmDocument.PhotoRecord record) {
        Query query = Query.query(Criteria.where("farmId").is(farmId));
        Update update = new Update().push("photoHistory",
            FarmDocumentEntity.PhotoRecordDoc.builder()
                .photoId(record.getPhotoId())
                .type(record.getType())
                .url(record.getUrl())
                .lat(record.getLat())
                .lng(record.getLng())
                .uploadedAt(record.getUploadedAt())
                .build());
        mongoTemplate.updateFirst(query, update, FarmDocumentEntity.class);
        log.info("Appended photo record for farm: {}", farmId);
    }

    @Override
    public void appendAgronomistReport(String farmId, FarmDocument.AgronomistReport report) {
        Query query = Query.query(Criteria.where("farmId").is(farmId));
        Update update = new Update().push("agronomistReports",
            FarmDocumentEntity.AgronomistReportDoc.builder()
                .reportId(report.getReportId())
                .agronomistId(report.getAgronomistId())
                .visitDate(report.getVisitDate())
                .diagnosis(report.getDiagnosis())
                .treatment(report.getTreatment())
                .rating(report.getRating())
                .build());
        mongoTemplate.updateFirst(query, update, FarmDocumentEntity.class);
    }

    @Override
    public void appendWeatherRecord(String farmId, FarmDocument.WeatherRecord record) {
        Query query = Query.query(Criteria.where("farmId").is(farmId));
        Update update = new Update().push("weatherLog",
            FarmDocumentEntity.WeatherRecordDoc.builder()
                .date(record.getDate())
                .rainfallMm(record.getRainfallMm())
                .tempC(record.getTempC())
                .isDryDay(record.getIsDryDay())
                .build());
        mongoTemplate.updateFirst(query, update, FarmDocumentEntity.class);
    }

    private FarmDocumentEntity toEntity(FarmDocument domain) {
        return FarmDocumentEntity.builder()
            .farmId(domain.getFarmId())
            .geoJsonPolygon(domain.getGeoJsonPolygon())
            .ndviHistory(domain.getNdviHistory().stream()
                .map(r -> FarmDocumentEntity.NdviReadingDoc.builder()
                    .date(r.getDate()).ndvi(r.getNdvi())
                    .cloudCoverage(r.getCloudCoverage()).build())
                .collect(Collectors.toList()))
            .photoHistory(domain.getPhotoHistory().stream()
                .map(r -> FarmDocumentEntity.PhotoRecordDoc.builder()
                    .photoId(r.getPhotoId()).type(r.getType()).url(r.getUrl())
                    .lat(r.getLat()).lng(r.getLng()).uploadedAt(r.getUploadedAt()).build())
                .collect(Collectors.toList()))
            .agronomistReports(domain.getAgronomistReports().stream()
                .map(r -> FarmDocumentEntity.AgronomistReportDoc.builder()
                    .reportId(r.getReportId()).agronomistId(r.getAgronomistId())
                    .visitDate(r.getVisitDate()).diagnosis(r.getDiagnosis())
                    .treatment(r.getTreatment()).rating(r.getRating()).build())
                .collect(Collectors.toList()))
            .weatherLog(domain.getWeatherLog().stream()
                .map(r -> FarmDocumentEntity.WeatherRecordDoc.builder()
                    .date(r.getDate()).rainfallMm(r.getRainfallMm())
                    .tempC(r.getTempC()).isDryDay(r.getIsDryDay()).build())
                .collect(Collectors.toList()))
            .build();
    }

    private FarmDocument toDomain(FarmDocumentEntity entity) {
        return FarmDocument.builder()
            .farmId(entity.getFarmId())
            .geoJsonPolygon(entity.getGeoJsonPolygon())
            .ndviHistory(entity.getNdviHistory().stream()
                .map(r -> FarmDocument.NdviReading.builder()
                    .date(r.getDate()).ndvi(r.getNdvi())
                    .cloudCoverage(r.getCloudCoverage()).build())
                .collect(Collectors.toList()))
            .photoHistory(entity.getPhotoHistory().stream()
                .map(r -> FarmDocument.PhotoRecord.builder()
                    .photoId(r.getPhotoId()).type(r.getType()).url(r.getUrl())
                    .lat(r.getLat()).lng(r.getLng()).uploadedAt(r.getUploadedAt()).build())
                .collect(Collectors.toList()))
            .agronomistReports(entity.getAgronomistReports().stream()
                .map(r -> FarmDocument.AgronomistReport.builder()
                    .reportId(r.getReportId()).agronomistId(r.getAgronomistId())
                    .visitDate(r.getVisitDate()).diagnosis(r.getDiagnosis())
                    .treatment(r.getTreatment()).rating(r.getRating()).build())
                .collect(Collectors.toList()))
            .weatherLog(entity.getWeatherLog().stream()
                .map(r -> FarmDocument.WeatherRecord.builder()
                    .date(r.getDate()).rainfallMm(r.getRainfallMm())
                    .tempC(r.getTempC()).isDryDay(r.getIsDryDay()).build())
                .collect(Collectors.toList()))
            .build();
    }
}
