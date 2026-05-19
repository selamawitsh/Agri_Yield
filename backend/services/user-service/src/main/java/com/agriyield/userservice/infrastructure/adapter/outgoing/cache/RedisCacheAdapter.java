package com.agriyield.userservice.infrastructure.adapter.outgoing.cache;

import com.agriyield.userservice.core.port.outgoing.CachePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheAdapter implements CachePort {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("Cache set: {} = {}", key, value);
        } catch (Exception e) {
            log.error("Failed to set cache key: {}", key, e);
        }
    }
    
    @Override
    public Optional<Object> get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Cache get: {} = {}", key, value);
            return Optional.ofNullable(value);
        } catch (Exception e) {
            log.error("Failed to get cache key: {}", key, e);
            return Optional.empty();
        }
    }
    
    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Cache delete: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete cache key: {}", key, e);
        }
    }
    
    @Override
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Failed to check cache key existence: {}", key, e);
            return false;
        }
    }
    
    @Override
    public void increment(String key) {
        try {
            redisTemplate.opsForValue().increment(key);
            log.debug("Cache increment: {}", key);
        } catch (Exception e) {
            log.error("Failed to increment cache key: {}", key, e);
        }
    }
    
    @Override
    public long getIncrement(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof Long) {
                return (Long) value;
            }
            if (value instanceof Integer) {
                return ((Integer) value).longValue();
            }
            return 0;
        } catch (Exception e) {
            log.error("Failed to get increment value for key: {}", key, e);
            return 0;
        }
    }
}
