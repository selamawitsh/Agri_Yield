package com.agriyield.voucherservice.application.port.outgoing;

import java.util.List;
import java.util.UUID;

public interface MerchantServicePort {

    /** Returns the product categories this merchant is certified to sell. */
    List<String> getMerchantCategories(UUID merchantId);

    /** Returns [lat, lng] of the merchant's store GPS. */
    double[] getMerchantLocation(UUID merchantId);

    /**
     * Check if merchant has sufficient stock for this voucher category.
     * Returns InventoryCheckResult with available=true if ok,
     * or available=false with errorCode if not.
     */
    InventoryCheckResult checkInventory(UUID merchantId, String category,
                                        double requiredQuantity);

    /**
     * Deduct stock after successful voucher redemption.
     * Called only after all 6 checks pass.
     */
    void deductInventory(UUID merchantId, String category, double quantity);

    record InventoryCheckResult(
        boolean available,
        String productName,
        double availableQuantity,
        String unit,
        String productId,
        String errorCode,
        String errorMessage
    ) {}
}
