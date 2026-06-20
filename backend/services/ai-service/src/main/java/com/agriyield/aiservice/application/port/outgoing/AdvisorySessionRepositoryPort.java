package com.agriyield.aiservice.application.port.outgoing;

import com.agriyield.aiservice.domain.model.AdvisorySession;
import java.util.List;

public interface AdvisorySessionRepositoryPort {

    AdvisorySession save(AdvisorySession session);

    List<AdvisorySession> findByFarmerId(String farmerId);

    List<AdvisorySession> findByFarmId(String farmId);
}