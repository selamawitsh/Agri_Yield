package com.agriyield.userservice.application.port.outgoing;

import java.util.UUID;

public interface MerchantProfileRepositoryPort {
    void createDefaultProfile(UUID userId);
}
