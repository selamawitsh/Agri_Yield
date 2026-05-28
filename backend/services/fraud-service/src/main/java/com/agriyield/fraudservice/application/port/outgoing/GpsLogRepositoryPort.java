package com.agriyield.fraudservice.application.port.outgoing;

import com.agriyield.fraudservice.domain.model.GpsLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GpsLogRepositoryPort {

    GpsLog save(GpsLog log);

    Optional<GpsLog> findLatestByEntityId(UUID entityId, String entityType);

    List<GpsLog> findRecentByEntityId(UUID entityId, String entityType, int limit);
}
