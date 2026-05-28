package com.agriyield.geospatialservice.application.port.outgoing;

import com.agriyield.geospatialservice.domain.model.NdviReading;
import com.agriyield.geospatialservice.domain.model.YieldPrediction;

import java.util.UUID;

public interface GeospatialEventPublisherPort {

    void publishNdviUpdated(NdviReading reading, double changeFromPrevious);

    void publishYieldPredicted(YieldPrediction prediction);

    void publishHarvestPredicted(UUID farmId,
                                  String estimatedDateFrom,
                                  String estimatedDateTo,
                                  double currentNdvi);
}
