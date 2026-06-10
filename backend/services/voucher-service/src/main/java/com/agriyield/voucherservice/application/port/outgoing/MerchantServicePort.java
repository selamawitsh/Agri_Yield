package com.agriyield.voucherservice.application.port.outgoing;

import java.util.List;
import java.util.UUID;

public interface MerchantServicePort {
    /** Returns the product categories this merchant is certified to sell. */
    List<String> getMerchantCategories(UUID merchantId);

    /** Returns [lat, lng] of the merchant's store GPS. */
    double[] getMerchantLocation(UUID merchantId);
}
