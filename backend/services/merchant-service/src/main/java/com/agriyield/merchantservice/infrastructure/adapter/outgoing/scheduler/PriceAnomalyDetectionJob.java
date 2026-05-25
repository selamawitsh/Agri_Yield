package com.agriyield.merchantservice.infrastructure.adapter.outgoing.scheduler;

import com.agriyield.merchantservice.application.port.outgoing.*;
import com.agriyield.merchantservice.domain.model.MerchantProfile;
import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import com.agriyield.merchantservice.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceAnomalyDetectionJob {

    private final MerchantProfileRepositoryPort merchantProfileRepository;
    private final ProductRepositoryPort productRepository;
    private final PriceAnomalyRepositoryPort priceAnomalyRepository;
    private final PriceIndexCachePort priceIndexCache;
    private final EventPublisherPort eventPublisher;

    @Value("${app.price-anomaly.threshold-pct:15.0}")
    private double thresholdPct;

    // Runs every night at 23:00 UTC as per SRS
    @Scheduled(cron = "0 0 23 * * *", zone = "UTC")
    public void detectPriceAnomalies() {
        log.info("Starting nightly price anomaly detection job");

        List<MerchantProfile> allMerchants = merchantProfileRepository.findAll();

        // Group products by (kebeleCode, category)
        Map<String, List<Product>> grouped = new HashMap<>();
        for (MerchantProfile merchant : allMerchants) {
            List<Product> products = productRepository.findByMerchantId(merchant.getId());
            for (Product product : products) {
                if (!product.isAvailable()) continue;
                String key = merchant.getId() + "|" + product.getProductCategory().name();
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(product);
            }
        }

        // For each merchant per category, compute median and detect anomalies
        for (MerchantProfile merchant : allMerchants) {
            List<Product> products = productRepository.findByMerchantId(merchant.getId());
            Map<String, List<Product>> byCategory = products.stream()
                    .filter(Product::isAvailable)
                    .collect(Collectors.groupingBy(p -> p.getProductCategory().name()));

            for (Map.Entry<String, List<Product>> entry : byCategory.entrySet()) {
                String category = entry.getKey();
                List<BigDecimal> prices = entry.getValue().stream()
                        .map(Product::getCurrentPriceEtb)
                        .sorted()
                        .collect(Collectors.toList());

                BigDecimal median = calculateMedian(prices);
                // Use a placeholder kebeleCode — in production wire merchant.kebeleCode
                String kebeleCode = "DEFAULT";
                priceIndexCache.storeRegionalMedian(kebeleCode, category, median);

                BigDecimal threshold = median.multiply(
                        BigDecimal.valueOf(1 + thresholdPct / 100));

                for (Product product : entry.getValue()) {
                    if (product.getCurrentPriceEtb().compareTo(threshold) > 0) {
                        BigDecimal deviation = product.getCurrentPriceEtb()
                                .subtract(median)
                                .divide(median, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));

                        PriceAnomaly anomaly = PriceAnomaly.builder()
                                .merchantId(merchant.getId())
                                .productId(product.getId())
                                .merchantPriceEtb(product.getCurrentPriceEtb())
                                .regionalMedianEtb(median)
                                .deviationPct(deviation)
                                .flaggedAt(OffsetDateTime.now())
                                .build();

                        priceAnomalyRepository.save(anomaly);

                        Map<String, Object> event = new HashMap<>();
                        event.put("event_type", "merchant.price.anomaly");
                        event.put("merchant_id", merchant.getId().toString());
                        event.put("product_id", product.getId().toString());
                        event.put("merchant_price_etb", product.getCurrentPriceEtb());
                        event.put("regional_median_etb", median);
                        event.put("deviation_pct", deviation);
                        event.put("timestamp", OffsetDateTime.now().toString());
                        eventPublisher.publish("fraud.exchange", "merchant.price.anomaly", event);

                        log.warn("Price anomaly flagged: merchant={} product={} deviation={}%",
                                merchant.getId(), product.getId(), deviation);
                    }
                }
            }
        }

        log.info("Price anomaly detection job completed");
    }

    private BigDecimal calculateMedian(List<BigDecimal> sortedPrices) {
        if (sortedPrices.isEmpty()) return BigDecimal.ZERO;
        int size = sortedPrices.size();
        if (size % 2 == 1) {
            return sortedPrices.get(size / 2);
        } else {
            return sortedPrices.get(size / 2 - 1)
                    .add(sortedPrices.get(size / 2))
                    .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        }
    }
}
