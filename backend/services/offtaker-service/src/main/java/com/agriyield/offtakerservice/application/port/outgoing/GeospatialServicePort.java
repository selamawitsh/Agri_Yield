package com.agriyield.offtakerservice.application.port.outgoing;

import java.util.List;
import java.util.Map;

public interface GeospatialServicePort {
    Map<String, Object> getFarmContext(String farmId);
    Map<String, Object> getHarvestReadiness(String farmId);

    /**
     * Full NDVI time-series history for the off-taker farm detail view.
     * SRS UC-OFF-02 requires "Full NDVI history".
     */
    List<Map<String, Object>> getNdviHistory(String farmId, int limitDays);
}
