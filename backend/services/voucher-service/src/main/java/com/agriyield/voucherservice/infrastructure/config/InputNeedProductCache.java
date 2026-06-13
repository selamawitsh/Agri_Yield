package com.agriyield.voucherservice.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Caches input need product details (category, name, quantity, sequence)
 * from the input.needs.created event so voucher generation can use them.
 * TTL: 7 days (listings expire in 30 days, well within range).
 */
@Slf4j
@Component
public class InputNeedProductCache {

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private static final String PREFIX = "input_need:items:";
    private static final Duration TTL  = Duration.ofDays(7);

    public InputNeedProductCache(StringRedisTemplate redis) {
        this.redis  = redis;
        this.mapper = new ObjectMapper();
    }

    public void store(String inputNeedId, List<Map<String, Object>> items) {
        try {
            String json = mapper.writeValueAsString(items);
            redis.opsForValue().set(PREFIX + inputNeedId, json, TTL);
            log.info("Cached {} items for input_need_id={}", items.size(), inputNeedId);
        } catch (Exception e) {
            log.warn("Failed to cache input need items for {}: {}", inputNeedId, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> get(String inputNeedId) {
        try {
            String json = redis.opsForValue().get(PREFIX + inputNeedId);
            if (json == null) return List.of();
            return mapper.readValue(json, List.class);
        } catch (Exception e) {
            log.warn("Failed to read cached items for {}: {}", inputNeedId, e.getMessage());
            return List.of();
        }
    }
}
