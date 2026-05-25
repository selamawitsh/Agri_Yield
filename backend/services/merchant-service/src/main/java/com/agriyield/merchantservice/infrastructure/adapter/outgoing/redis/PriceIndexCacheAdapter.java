package com.agriyield.merchantservice.infrastructure.adapter.outgoing.redis;

import com.agriyield.merchantservice.application.port.outgoing.PriceIndexCachePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PriceIndexCacheAdapter implements PriceIndexCachePort {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.redis.price-index-ttl-hours:25}")
    private long ttlHours;

    private String buildKey(String kebeleCode, String category) {
        return "regional_price:" + kebeleCode + ":" + category;
    }

    @Override
    public void storeRegionalMedian(String kebeleCode, String category, BigDecimal median) {
        redisTemplate.opsForValue().set(
                buildKey(kebeleCode, category),
                median.toPlainString(),
                Duration.ofHours(ttlHours)
        );
    }

    @Override
    public Optional<BigDecimal> getRegionalMedian(String kebeleCode, String category) {
        String value = redisTemplate.opsForValue().get(buildKey(kebeleCode, category));
        if (value == null) return Optional.empty();
        return Optional.of(new BigDecimal(value));
    }
}
