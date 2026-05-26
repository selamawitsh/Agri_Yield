package com.agriyield.voucherservice.application.port.outgoing;

import java.util.UUID;

public interface UserServicePort {

    boolean verifyMerchantExists(UUID merchantId);

    boolean verifyFarmerExists(UUID farmerId);
}
