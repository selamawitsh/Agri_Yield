package com.agriyield.userservice.application.service;

import com.agriyield.userservice.core.domain.model.BankAccount;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.outgoing.BankAccountRepositoryPort;
import com.agriyield.userservice.core.port.outgoing.NotificationPort;
import com.agriyield.userservice.core.port.outgoing.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {
    
    private final BankAccountRepositoryPort bankAccountRepository;
    private final UserRepositoryPort userRepository;
    private final NotificationPort notificationPort;
    
    @Transactional
    public BankAccount addBankAccount(UUID userId, String accountType, String accountNumber, String accountHolderName) {
        log.info("Adding bank account for user: {}, type: {}, number: {}", userId, accountType, accountNumber);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Clean and validate account number
        String cleanedNumber = cleanAccountNumber(accountNumber);
        validateAccountNumber(accountType, cleanedNumber);
        
        // Check if account already exists
        var existing = bankAccountRepository.findByUserIdAndAccountType(userId, accountType);
        if (existing.isPresent()) {
            throw new RuntimeException("Bank account already exists for this type");
        }
        
        // Create new bank account (not verified yet)
        BankAccount account = BankAccount.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .accountType(accountType)
            .accountNumber(cleanedNumber)
            .accountHolderName(accountHolderName != null ? accountHolderName : user.getPhone())
            .isVerified(false)
            .isDefault(bankAccountRepository.countVerifiedAccounts(userId) == 0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        BankAccount saved = bankAccountRepository.save(account);
        
        // Send test deposit (1 ETB)
        sendTestDeposit(account);
        
        log.info("Bank account added for user: {}, pending verification", userId);
        return saved;
    }
    
    private String cleanAccountNumber(String accountNumber) {
        if (accountNumber == null) return null;
        // Remove all non-digit characters (+, spaces, dashes, etc.)
        String cleaned = accountNumber.replaceAll("[^0-9]", "");
        // Remove leading '251' if present (Ethiopia country code)
        if (cleaned.startsWith("251") && cleaned.length() == 12) {
            cleaned = cleaned.substring(3);
        }
        return cleaned;
    }
    
    private void validateAccountNumber(String accountType, String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new RuntimeException("Account number is required");
        }
        
        if ("TELEBIRR".equals(accountType)) {
            // Telebirr accounts: 9 or 10 digits (Ethiopian phone without country code)
            if (!accountNumber.matches("^[0-9]{9,10}$")) {
                throw new RuntimeException("Invalid Telebirr account number. Must be 9 or 10 digits (e.g., 912345678 or 0912345678)");
            }
        } else if ("CBE".equals(accountType)) {
            // CBE accounts: 10-16 digits
            if (!accountNumber.matches("^[0-9]{10,16}$")) {
                throw new RuntimeException("Invalid CBE account number. Must be 10-16 digits.");
            }
        }
    }
    
    @Transactional
    public BankAccount verifyBankAccount(UUID accountId, String verificationCode) {
        log.info("Verifying bank account: {} with code: {}", accountId, verificationCode);
        
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Bank account not found"));
        
        if (!isValidVerificationCode(verificationCode)) {
            throw new RuntimeException("Invalid verification code");
        }
        
        account.markVerified();
        BankAccount verified = bankAccountRepository.save(account);
        
        // Send notification
        User user = userRepository.findById(account.getUserId()).orElse(null);
        if (user != null) {
            notificationPort.sendSms(user.getPhone(), 
                "Agri-Yield: Your " + account.getAccountType() + " account (" + 
                maskAccountNumber(account.getAccountNumber()) + 
                ") has been verified and is ready for transactions.");
        }
        
        log.info("Bank account verified: {}", accountId);
        return verified;
    }
    
    @Transactional
    public BankAccount setDefaultAccount(UUID userId, UUID accountId) {
        log.info("Setting default account for user: {}, account: {}", userId, accountId);
        
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Bank account not found"));
        
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("Account does not belong to user");
        }
        
        if (!account.isVerified()) {
            throw new RuntimeException("Cannot set unverified account as default");
        }
        
        // Clear default flag for all user's accounts
        bankAccountRepository.clearDefaultFlag(userId);
        
        // Set this account as default
        account.setDefault(true);
        account.setUpdatedAt(LocalDateTime.now());
        
        BankAccount updated = bankAccountRepository.save(account);
        
        log.info("Default account set for user: {}", userId);
        return updated;
    }
    
    public List<BankAccount> getUserBankAccounts(UUID userId) {
        return bankAccountRepository.findByUserId(userId);
    }
    
    public BankAccount getDefaultBankAccount(UUID userId) {
        return bankAccountRepository.findDefaultByUserId(userId)
            .orElse(null);
    }
    
    @Transactional
    public void deleteBankAccount(UUID userId, UUID accountId) {
        log.info("Deleting bank account: {} for user: {}", accountId, userId);
        
        BankAccount account = bankAccountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Bank account not found"));
        
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("Account does not belong to user");
        }
        
        if (account.isDefault()) {
            throw new RuntimeException("Cannot delete default account. Set another account as default first.");
        }
        
        bankAccountRepository.deleteById(accountId);
        
        log.info("Bank account deleted: {}", accountId);
    }
    
    private void sendTestDeposit(BankAccount account) {
        log.info("Sending 1 ETB test deposit to {} account: {}", 
                 account.getAccountType(), maskAccountNumber(account.getAccountNumber()));
        
        String demoCode = "ETB1";
        log.info("DEMO: Test deposit sent. Use verification code: {} to verify account", demoCode);
        
        // Notify user about test deposit
        User user = userRepository.findById(account.getUserId()).orElse(null);
        if (user != null) {
            notificationPort.sendSms(
                user.getPhone(),
                "Agri-Yield: 1 ETB test deposit sent to your " + account.getAccountType() + 
                " account ending in " + account.getAccountNumber().substring(Math.max(0, account.getAccountNumber().length() - 4)) + 
                ". Use code 'ETB1' to verify your account."
            );
        }
    }
    
    private boolean isValidVerificationCode(String code) {
        // For demo, accept "ETB1" or "1"
        return "ETB1".equals(code) || "1".equals(code);
    }
    
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        String last4 = accountNumber.substring(accountNumber.length() - 4);
        return "****" + last4;
    }
}
