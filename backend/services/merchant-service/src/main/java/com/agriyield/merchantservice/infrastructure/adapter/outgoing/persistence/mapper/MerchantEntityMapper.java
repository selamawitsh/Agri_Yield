package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.mapper;

import com.agriyield.merchantservice.domain.enums.ProductCategory;
import com.agriyield.merchantservice.domain.model.PriceAnomaly;
import com.agriyield.merchantservice.domain.model.PriceHistory;
import com.agriyield.merchantservice.domain.model.Product;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.PriceAnomalyEntity;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.PriceHistoryEntity;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class MerchantEntityMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        return Product.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .productName(entity.getProductName())
                .productCategory(ProductCategory.valueOf(entity.getProductCategory()))
                .unit(entity.getUnit())
                .currentPriceEtb(entity.getCurrentPriceEtb())
                .isAvailable(entity.isAvailable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        return ProductEntity.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .productName(domain.getProductName())
                .productCategory(domain.getProductCategory().name())
                .unit(domain.getUnit())
                .currentPriceEtb(domain.getCurrentPriceEtb())
                .isAvailable(domain.isAvailable())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public PriceHistory toDomain(PriceHistoryEntity entity) {
        if (entity == null) return null;
        return PriceHistory.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .oldPriceEtb(entity.getOldPriceEtb())
                .newPriceEtb(entity.getNewPriceEtb())
                .changedAt(entity.getChangedAt())
                .changedBy(entity.getChangedBy())
                .build();
    }

    public PriceHistoryEntity toEntity(PriceHistory domain) {
        return PriceHistoryEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .oldPriceEtb(domain.getOldPriceEtb())
                .newPriceEtb(domain.getNewPriceEtb())
                .changedAt(domain.getChangedAt())
                .changedBy(domain.getChangedBy())
                .build();
    }

    public PriceAnomaly toDomain(PriceAnomalyEntity entity) {
        if (entity == null) return null;
        return PriceAnomaly.builder()
                .id(entity.getId())
                .merchantId(entity.getMerchantId())
                .productId(entity.getProductId())
                .merchantPriceEtb(entity.getMerchantPriceEtb())
                .regionalMedianEtb(entity.getRegionalMedianEtb())
                .deviationPct(entity.getDeviationPct())
                .flaggedAt(entity.getFlaggedAt())
                .resolvedAt(entity.getResolvedAt())
                .build();
    }

    public PriceAnomalyEntity toEntity(PriceAnomaly domain) {
        return PriceAnomalyEntity.builder()
                .id(domain.getId())
                .merchantId(domain.getMerchantId())
                .productId(domain.getProductId())
                .merchantPriceEtb(domain.getMerchantPriceEtb())
                .regionalMedianEtb(domain.getRegionalMedianEtb())
                .deviationPct(domain.getDeviationPct())
                .flaggedAt(domain.getFlaggedAt())
                .resolvedAt(domain.getResolvedAt())
                .build();
    }
}
