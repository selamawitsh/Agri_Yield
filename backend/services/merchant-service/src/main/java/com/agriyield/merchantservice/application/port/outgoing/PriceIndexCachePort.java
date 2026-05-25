package com.agriyield.merchantservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceIndexCachePort {
    void storeRegionalMedian(String kebeleCode, String category, BigDecimal median);
    Optional<BigDecimal> getRegionalMedian(String kebeleCode, String category);
}
