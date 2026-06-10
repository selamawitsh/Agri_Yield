package com.agriyield.offtakerservice.application.port.outgoing;

import java.util.Map;

public interface FarmServicePort {
    Map<String, Object> getFarmById(String farmId);
}
