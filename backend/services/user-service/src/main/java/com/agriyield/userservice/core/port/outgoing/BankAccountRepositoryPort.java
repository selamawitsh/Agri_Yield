package com.agriyield.userservice.core.port.outgoing;

import com.agriyield.userservice.core.domain.model.BankAccount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepositoryPort {
    BankAccount save(BankAccount account);
    Optional<BankAccount> findById(UUID id);
    List<BankAccount> findByUserId(UUID userId);
    Optional<BankAccount> findByUserIdAndAccountType(UUID userId, String accountType);
    Optional<BankAccount> findDefaultByUserId(UUID userId);
    void deleteById(UUID id);
    void clearDefaultFlag(UUID userId);
    long countVerifiedAccounts(UUID userId);
}
