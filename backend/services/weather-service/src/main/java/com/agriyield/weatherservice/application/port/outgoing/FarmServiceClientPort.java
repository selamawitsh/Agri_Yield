package com.agriyield.weatherservice.application.port.outgoing;

import java.util.List;
import java.util.UUID;

public interface FarmServiceClientPort {
    List<UUID> getActiveFarmIds();
    double[] getFarmCoordinates(UUID farmId); // returns [lat, lng]
}
