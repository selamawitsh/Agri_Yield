package com.agriyield.merchantservice.presentation.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MerchantAnalyticsResponse {
    private long totalProducts;
    private long availableProducts;
    private long priceAnomaliesCount;
    private BigDecimal averageProductPrice;
}
