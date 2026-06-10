package com.agriyield.offtakerservice.application.port.outgoing;

import java.util.Map;

public interface GeospatialServicePort {
    Map<String, Object> getFarmContext(String farmId);
    Map<String, Object> getHarvestReadiness(String farmId);
}
