package com.agriyield.escrowservice.application.port.outgoing;

import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.domain.model.EscrowRelease;

public interface EventPublisherPort {

    void publishEscrowLocked(EscrowAccount escrowAccount);

    void publishEscrowPartiallyReleased(EscrowAccount escrowAccount, EscrowRelease release);

    void publishEscrowFullyReleased(EscrowAccount escrowAccount);

    void publishEscrowCancelled(EscrowAccount escrowAccount);
}