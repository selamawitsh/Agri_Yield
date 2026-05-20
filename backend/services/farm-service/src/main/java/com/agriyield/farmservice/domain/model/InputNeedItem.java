package com.agriyield.farmservice.domain.model;

import com.agriyield.farmservice.domain.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputNeedItem {

    private UUID id;
    private UUID inputNeedId;

    private ProductCategory productCategory;
    private String productName;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal estimatedPriceEtb;

    // 1 = first to unlock, enforces agronomic order (SRS Page 20)
    private Integer sequenceOrder;
}
