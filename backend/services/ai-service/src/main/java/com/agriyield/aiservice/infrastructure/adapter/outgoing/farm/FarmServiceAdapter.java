package com.agriyield.aiservice.infrastructure.adapter.outgoing.farm;

import com.agriyield.aiservice.application.port.outgoing.FarmServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FarmServiceAdapter implements FarmServicePort {

    @Override
    public FarmData getFarmById(String farmId) {
        // TODO: Implement actual farm service call (possibly via REST API to farm-management service)
        log.warn("Using default farm data for farm: {}. Real implementation needed!", farmId);
        
        return new FarmData(
            farmId,                  // farmId
            "default-farmer-id",     // farmerId
            "WHEAT",                 // cropType
            "Amhara",                // region
            1.5,                     // areaHectares
            "ACTIVE",                // status
            "030601"                 // kebeleCode
        );
    }
}
