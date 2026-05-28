package com.agriyield.investmentservice.application.port.outgoing;

import java.util.List;
import java.util.UUID;

public interface GeospatialServicePort {

    record NdviHistoryPoint(
        String date,
        double ndviValue,
        double cloudCoverage
    ) {}

    List<NdviHistoryPoint> getNdviTimeSeries(UUID farmId, int limitDays);
}
