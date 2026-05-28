package com.agriyield.geospatialservice.application.port.outgoing;

import java.time.Duration;
import java.util.Optional;

public interface RedisPort {
    void set(String key, String value, Duration ttl);
    Optional<String> get(String key);
    boolean exists(String key);
    void delete(String key);
}
