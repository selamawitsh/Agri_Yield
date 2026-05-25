package com.agriyield.userservice.application.port.incoming;

import com.agriyield.userservice.domain.model.BankAccount;
import com.agriyield.userservice.domain.model.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserServicePort {

    // US-06 — View own profile
    User getCurrentUser(UUID userId);

    // US-07 — Update profile
    User updateProfile(UUID userId, Map<String, Object> updates);

    // US-08 — Link bank account
    BankAccount addBankAccount(UUID userId, String accountType,
                               String accountNumber, String holderName);

    BankAccount verifyBankAccount(UUID userId, UUID accountId,
                                  String verificationCode);

    BankAccount setDefaultBankAccount(UUID userId, UUID accountId);

    List<BankAccount> getBankAccounts(UUID userId);

    void deleteBankAccount(UUID userId, UUID accountId);

    // US-09 — Get user by ID (admin or internal)
    User getUserById(UUID userId);
}
