package com.agriyield.escrowservice.application.port.outgoing;

import com.agriyield.escrowservice.domain.model.EscrowRelease;

import java.util.List;
import java.util.UUID;

public interface EscrowReleaseRepositoryPort {

    EscrowRelease save(EscrowRelease release);

    List<EscrowRelease> findByEscrowAccountId(UUID escrowAccountId);
}