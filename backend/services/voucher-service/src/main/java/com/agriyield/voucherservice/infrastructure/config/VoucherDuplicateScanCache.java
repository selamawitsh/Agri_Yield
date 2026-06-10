package com.agriyield.voucherservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * SRS §3.5.3 Check #2 — Redis duplicate scan detection.
 * Key: voucher:scanned:{voucherCode}  TTL: 24h
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherDuplicateScanCache {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.voucher.duplicate-scan-ttl-hours:24}")
    private int ttlHours;

    private static final String PREFIX = "voucher:scanned:";

    public boolean isDuplicate(String voucherCode) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + voucherCode));
        } catch (Exception e) {
            log.warn("Redis duplicate check failed for {} — allowing scan: {}", voucherCode, e.getMessage());
            return false;
        }
    }

    public void markScanned(String voucherCode) {
        try {
            redisTemplate.opsForValue().set(
                PREFIX + voucherCode,
                LocalDateTime.now().toString(),
                Duration.ofHours(ttlHours));
        } catch (Exception e) {
            log.warn("Redis mark-scanned failed for {}: {}", voucherCode, e.getMessage());
        }
    }

    public String getFirstScanTimestamp(String voucherCode) {
        try {
            return redisTemplate.opsForValue().get(PREFIX + voucherCode);
        } catch (Exception e) {
            return null;
        }
    }
}
