package com.agriyield.escrowservice.application.port.outgoing;

import com.agriyield.escrowservice.domain.model.EscrowTransaction;

import java.util.List;
import java.util.UUID;

public interface EscrowTransactionRepositoryPort {

    EscrowTransaction save(EscrowTransaction transaction);

    List<EscrowTransaction> findByEscrowAccountId(UUID escrowAccountId);
}