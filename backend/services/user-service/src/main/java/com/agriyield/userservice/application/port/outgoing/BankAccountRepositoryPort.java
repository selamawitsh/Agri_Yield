package com.agriyield.userservice.application.port.outgoing;

import com.agriyield.userservice.domain.model.BankAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepositoryPort {

    BankAccount save(BankAccount bankAccount);

    Optional<BankAccount> findById(UUID id);

    List<BankAccount> findByUserId(UUID userId);

    Optional<BankAccount> findDefaultByUserId(UUID userId);

    void deleteById(UUID id);

    void clearDefaultForUser(UUID userId);
}
