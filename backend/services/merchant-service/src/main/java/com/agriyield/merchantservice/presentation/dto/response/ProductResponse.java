package com.agriyield.merchantservice.presentation.dto.response;

import com.agriyield.merchantservice.domain.model.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private UUID merchantId;
    private String productName;
    private String productCategory;
    private String unit;
    private BigDecimal currentPriceEtb;
    private boolean isAvailable;
    private BigDecimal quantityInStock;
    private String unitOfMeasure;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static ProductResponse from(Product domain) {
        return ProductResponse.builder()
            .id(domain.getId())
            .merchantId(domain.getMerchantId())
            .productName(domain.getProductName())
            .productCategory(domain.getProductCategory().name())
            .unit(domain.getUnit())
            .currentPriceEtb(domain.getCurrentPriceEtb())
            .isAvailable(domain.isAvailable())
            .quantityInStock(domain.getQuantityInStock() != null
                ? domain.getQuantityInStock() : BigDecimal.ZERO)
            .unitOfMeasure(domain.getUnitOfMeasure() != null
                ? domain.getUnitOfMeasure() : domain.getUnit())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}
