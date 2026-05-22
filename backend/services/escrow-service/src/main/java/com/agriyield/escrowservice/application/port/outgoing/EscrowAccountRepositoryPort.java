package com.agriyield.escrowservice.application.port.outgoing;

import com.agriyield.escrowservice.domain.model.EscrowAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EscrowAccountRepositoryPort {

    EscrowAccount save(EscrowAccount escrowAccount);

    Optional<EscrowAccount> findById(UUID id);

    Optional<EscrowAccount> findByInvestmentId(UUID investmentId);

    List<EscrowAccount> findByFarmerId(UUID farmerId);

    List<EscrowAccount> findByInvestorId(UUID investorId);
}