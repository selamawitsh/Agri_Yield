package com.agriyield.investmentservice.infrastructure.adapter.outgoing.grpc;

import com.agriyield.geospatialservice.grpc.GeospatialServiceGrpc;
import com.agriyield.geospatialservice.grpc.GeospatialServiceProto;
import com.agriyield.investmentservice.application.port.outgoing.GeospatialServicePort;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GeospatialServiceGrpcClient implements GeospatialServicePort {

    @GrpcClient("geospatial-service")
    private GeospatialServiceGrpc.GeospatialServiceBlockingStub geospatialStub;

    @Override
    public List<NdviHistoryPoint> getNdviTimeSeries(UUID farmId, int limitDays) {
        log.info("gRPC: getNdviTimeSeries farm={} days={}", farmId, limitDays);
        GeospatialServiceProto.NdviTimeSeriesResponse response =
            geospatialStub.getNdviTimeSeries(
                GeospatialServiceProto.NdviTimeSeriesRequest.newBuilder()
                    .setFarmId(farmId.toString())
                    .setLimitDays(limitDays)
                    .build());
        return response.getReadingsList().stream()
            .map(r -> new NdviHistoryPoint(
                r.getDate(),
                r.getNdviValue(),
                r.getCloudCoverage()))
            .collect(Collectors.toList());
    }
}
