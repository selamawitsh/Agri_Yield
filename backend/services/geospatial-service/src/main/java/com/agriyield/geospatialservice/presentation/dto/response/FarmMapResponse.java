package com.agriyield.geospatialservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class FarmMapResponse {
    private UUID farmId;
    private String geoJsonPolygon;
    private double centroidLat;
    private double centroidLng;
    private double areaHectares;
    private NdviReadingResponse latestNdvi;
}
