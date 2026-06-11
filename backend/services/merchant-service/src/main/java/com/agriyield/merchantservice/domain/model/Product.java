package com.agriyield.merchantservice.domain.model;

import com.agriyield.merchantservice.domain.enums.ProductCategory;
import com.agriyield.merchantservice.domain.exception.BusinessException;
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
    private BigDecimal quantityInStock;
    private String unitOfMeasure;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * Deducts quantity from stock after a successful voucher redemption.
     * Throws BusinessException if insufficient stock.
     */
    public void deductStock(BigDecimal amount) {
        if (quantityInStock == null) quantityInStock = BigDecimal.ZERO;
        if (amount.compareTo(quantityInStock) > 0) {
            throw new BusinessException(
                "Insufficient stock. Required: " + amount +
                " " + unit + ", Available: " + quantityInStock + " " + unit,
                "INSUFFICIENT_STOCK");
        }
        this.quantityInStock = this.quantityInStock.subtract(amount);
        if (this.quantityInStock.compareTo(BigDecimal.ZERO) == 0) {
            this.isAvailable = false;
        }
    }

    /** Returns true if merchant has enough stock for the given amount. */
    public boolean hasSufficientStock(BigDecimal required) {
        if (quantityInStock == null) return false;
        return quantityInStock.compareTo(required) >= 0;
    }
}
