package com.agriyield.offtakerservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.geospatialservice.grpc.GeospatialServiceGrpc;
import com.agriyield.geospatialservice.grpc.GeospatialServiceProto;
import com.agriyield.offtakerservice.application.port.outgoing.GeospatialServicePort;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeospatialServiceGrpcClient implements GeospatialServicePort {

    @GrpcClient("geospatial-service")
    private GeospatialServiceGrpc.GeospatialServiceBlockingStub geospatialStub;

    @Override
    public Map<String, Object> getFarmContext(String farmId) {
        try {
            GeospatialServiceProto.FarmContextResponse response = geospatialStub.getFarmContext(
                GeospatialServiceProto.FarmIdRequest.newBuilder()
                    .setFarmId(farmId)
                    .build());

            Map<String, Object> ctx = new HashMap<>();
            ctx.put("farmId",                      response.getFarmId());
            ctx.put("farmerId",                    response.getFarmerId());
            ctx.put("cropType",                    response.getCropType());
            ctx.put("areaHectares",                response.getAreaHectares());
            ctx.put("region",                      response.getRegion());
            ctx.put("kebeleCode",                  response.getKebeleCode());
            ctx.put("gpsCentroidLat",              response.getGpsCentroidLat());
            ctx.put("gpsCentroidLng",              response.getGpsCentroidLng());
            ctx.put("agriScore",                   response.getAgriScore());
            ctx.put("cropCycleId",                 response.getCropCycleId());
            ctx.put("cropCycleStatus",             response.getCropCycleStatus());
            ctx.put("currentNdvi",                 response.getCurrentNdvi());
            ctx.put("ndviHealthStatus",            response.getNdviHealthStatus());
            ctx.put("predictedYieldMeanQuintals",  response.getPredictedYieldMeanQuintals());
            ctx.put("yieldConfidencePct",          response.getYieldConfidencePct());
            return ctx;

        } catch (StatusRuntimeException e) {
            log.error("GeospatialService.getFarmContext failed farmId={}: {}", farmId, e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getHarvestReadiness(String farmId) {
        try {
            GeospatialServiceProto.HarvestReadinessResponse response =
                geospatialStub.predictHarvestReadiness(
                    GeospatialServiceProto.FarmIdRequest.newBuilder()
                        .setFarmId(farmId)
                        .build());

            Map<String, Object> result = new HashMap<>();
            result.put("isReady",         response.getIsReady());
            result.put("estimatedDateFrom", response.getEstimatedDateFrom());
            result.put("estimatedDateTo",   response.getEstimatedDateTo());
            result.put("currentNdvi",       response.getCurrentNdvi());
            result.put("peakNdvi",          response.getPeakNdvi());
            result.put("readinessSignal",   response.getReadinessSignal());
            return result;

        } catch (StatusRuntimeException e) {
            log.error("GeospatialService.getHarvestReadiness failed farmId={}: {}", farmId, e.getMessage());
            return Map.of("isReady", false);
        }
    }

    @Override
    public List<Map<String, Object>> getNdviHistory(String farmId, int limitDays) {
        try {
            GeospatialServiceProto.NdviTimeSeriesResponse response =
                geospatialStub.getNdviTimeSeries(
                    GeospatialServiceProto.NdviTimeSeriesRequest.newBuilder()
                        .setFarmId(farmId)
                        .setLimitDays(limitDays)
                        .build());

            List<Map<String, Object>> history = new ArrayList<>();
            for (GeospatialServiceProto.NdviDataPoint point : response.getReadingsList()) {
                Map<String, Object> reading = new HashMap<>();
                reading.put("date", point.getDate());
                reading.put("ndviValue", point.getNdviValue());
                reading.put("cloudCoverage", point.getCloudCoverage());
                history.add(reading);
            }
            return history;

        } catch (StatusRuntimeException e) {
            log.warn("GeospatialService.getNdviHistory failed farmId={}: {}", farmId, e.getMessage());
            return List.of();
        }
    }
}
