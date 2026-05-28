package com.agriyield.faydagateway.application.port.outgoing;

import java.time.Duration;
import java.util.Optional;

public interface CachePort {
    void set(String key, String value, Duration ttl);
    Optional<String> get(String key);
    void delete(String key);
}
