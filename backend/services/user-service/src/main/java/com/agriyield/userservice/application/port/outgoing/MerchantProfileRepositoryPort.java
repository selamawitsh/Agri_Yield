package com.agriyield.userservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface MerchantProfileRepositoryPort {

    void createDefaultProfile(UUID userId);

    void createMerchantProfile(
            UUID userId,
            String businessName,
            String businessLicenseNumber,
            BigDecimal storeGpsLat,
            BigDecimal storeGpsLng,
            String telebirrAccount,
            String kebeleCode
    );
}