package com.agriyield.userservice.application.service;

import com.agriyield.userservice.application.port.incoming.UserServicePort;
import com.agriyield.userservice.application.port.outgoing.*;
import com.agriyield.userservice.domain.enums.PreferredLanguage;
import com.agriyield.userservice.domain.exception.BusinessException;
import com.agriyield.userservice.domain.exception.ResourceNotFoundException;
import com.agriyield.userservice.domain.model.BankAccount;
import com.agriyield.userservice.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserServicePort {

    private final UserRepositoryPort userRepository;
    private final BankAccountRepositoryPort bankAccountRepository;
    private final InvestorProfileRepositoryPort investorProfileRepository;
    private final CachePort cachePort;
    private final NotificationPort notificationPort;

    // US-06
    @Override
    public User getCurrentUser(UUID userId) {
        return getUserById(userId);
    }

    // US-07
    @Override
    @Transactional
    public User updateProfile(UUID userId, Map<String, Object> updates) {
        log.info("Updating profile for user: {}", userId);
        User user = getUserById(userId);

        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        if (updates.containsKey("preferredLanguage")) {
            user.setPreferredLanguage(
                PreferredLanguage.fromCode(
                    (String) updates.get("preferredLanguage")));
        }
        if (updates.containsKey("riskTolerance")) {
            user.setRiskTolerance((String) updates.get("riskTolerance"));
            try {
                investorProfileRepository.updateRiskTolerance(
                    userId, (String) updates.get("riskTolerance"));
            } catch (Exception e) {
                log.warn("Could not update investor risk tolerance: {}",
                    e.getMessage());
            }
        }
        if (updates.containsKey("investmentGoal")) {
            user.setInvestmentGoal((String) updates.get("investmentGoal"));
            try {
                investorProfileRepository.updateInvestmentGoal(
                    userId, (String) updates.get("investmentGoal"));
            } catch (Exception e) {
                log.warn("Could not update investor goal: {}",
                    e.getMessage());
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        cachePort.delete("user:" + userId);
        return saved;
    }

    // US-08 — Add bank account
    @Override
    @Transactional
    public BankAccount addBankAccount(UUID userId, String accountType,
                                      String accountNumber,
                                      String holderName) {
        log.info("Adding bank account for user: {}, type: {}",
            userId, accountType);

        getUserById(userId); // verify user exists

        BankAccount account = BankAccount.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .accountType(accountType.toUpperCase())
            .accountNumber(accountNumber)
            .accountHolderName(holderName)
            .isVerified(false)
            .isDefault(false)
            .createdAt(LocalDateTime.now())
            .build();

        BankAccount saved = bankAccountRepository.save(account);

        // Send 1 ETB test deposit — stubbed, real impl via Telebirr/CBE API
        notificationPort.sendSms(accountNumber,
            "Agri-Yield: Verification code ETB1 sent to your account.");

        return saved;
    }

    // US-08 — Verify bank account
    @Override
    @Transactional
    public BankAccount verifyBankAccount(UUID userId, UUID accountId,
                                         String verificationCode) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "BankAccount", "id", accountId));

        if (!account.getUserId().equals(userId)) {
            throw new BusinessException(
                "Account does not belong to this user",
                "UNAUTHORIZED_ACCOUNT");
        }

        // SRS: verification code is ETB1 (stub for development)
        if (!"ETB1".equals(verificationCode)) {
            throw new BusinessException(
                "Invalid verification code", "INVALID_VERIFICATION_CODE");
        }

        account.setIsVerified(true);
        account.setVerifiedAt(LocalDateTime.now());
        return bankAccountRepository.save(account);
    }

    // US-08 — Set default bank account
    @Override
    @Transactional
    public BankAccount setDefaultBankAccount(UUID userId, UUID accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "BankAccount", "id", accountId));

        if (!account.getUserId().equals(userId)) {
            throw new BusinessException(
                "Account does not belong to this user",
                "UNAUTHORIZED_ACCOUNT");
        }

        bankAccountRepository.clearDefaultForUser(userId);
        account.setIsDefault(true);
        return bankAccountRepository.save(account);
    }

    // US-08 — List bank accounts
    @Override
    public List<BankAccount> getBankAccounts(UUID userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    // US-08 — Delete bank account
    @Override
    @Transactional
    public void deleteBankAccount(UUID userId, UUID accountId) {
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "BankAccount", "id", accountId));
        if (!account.getUserId().equals(userId)) {
            throw new BusinessException(
                "Account does not belong to this user",
                "UNAUTHORIZED_ACCOUNT");
        }
        bankAccountRepository.deleteById(accountId);
    }

    // US-09
    @Override
    public User getUserById(UUID userId) {
        String cacheKey = "user:" + userId;
        var cached = cachePort.get(cacheKey);
        if (cached.isPresent() && cached.get() instanceof User) {
            return (User) cached.get();
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User", "id", userId));
        cachePort.set(cacheKey, user, 5, TimeUnit.MINUTES);
        return user;
    }
}
