package com.agriyield.userservice.application.port.outgoing;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface CachePort {

    void set(String key, Object value, long timeout, TimeUnit unit);

    Optional<Object> get(String key);

    void delete(String key);

    boolean exists(String key);

    void increment(String key);

    long getIncrement(String key);
}
