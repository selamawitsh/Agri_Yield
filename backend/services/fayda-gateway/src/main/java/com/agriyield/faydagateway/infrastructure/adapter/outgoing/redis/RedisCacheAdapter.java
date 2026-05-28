package com.agriyield.faydagateway.infrastructure.adapter.outgoing.redis;

import com.agriyield.faydagateway.application.port.outgoing.CachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheAdapter implements CachePort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void set(String key, String value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            log.error("Redis SET failed for key={}: {}", key, e.getMessage());
        }
    }

    @Override
    public Optional<String> get(String key) {
        try {
            String value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("Redis GET failed for key={}: {}", key, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis DELETE failed for key={}: {}", key, e.getMessage());
        }
    }
}
