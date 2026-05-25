package com.agriyield.merchantservice.domain.model;

import com.agriyield.merchantservice.domain.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private UUID merchantId;
    private String productName;
    private ProductCategory productCategory;
    private String unit;
    private BigDecimal currentPriceEtb;
    private boolean isAvailable;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
