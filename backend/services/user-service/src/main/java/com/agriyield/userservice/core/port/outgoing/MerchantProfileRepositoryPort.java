package com.agriyield.userservice.core.port.outgoing;

import java.util.UUID;

public interface MerchantProfileRepositoryPort {
    void createDefaultProfile(UUID userId);
}
