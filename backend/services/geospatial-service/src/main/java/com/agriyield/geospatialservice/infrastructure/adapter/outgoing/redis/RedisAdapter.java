package com.agriyield.geospatialservice.infrastructure.adapter.outgoing.redis;

import com.agriyield.geospatialservice.application.port.outgoing.RedisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAdapter implements RedisPort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void set(String key, String value, Duration ttl) {
        try { redisTemplate.opsForValue().set(key, value, ttl); }
        catch (Exception e) { log.error("Redis SET failed key={}: {}", key, e.getMessage()); }
    }

    @Override
    public Optional<String> get(String key) {
        try { return Optional.ofNullable(redisTemplate.opsForValue().get(key)); }
        catch (Exception e) { log.error("Redis GET failed key={}: {}", key, e.getMessage()); return Optional.empty(); }
    }

    @Override
    public boolean exists(String key) {
        try { return Boolean.TRUE.equals(redisTemplate.hasKey(key)); }
        catch (Exception e) { return false; }
    }

    @Override
    public void delete(String key) {
        try { redisTemplate.delete(key); }
        catch (Exception e) { log.error("Redis DELETE failed key={}: {}", key, e.getMessage()); }
    }
}
