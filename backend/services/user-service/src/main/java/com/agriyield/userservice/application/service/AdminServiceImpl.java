package com.agriyield.userservice.application.service;

import com.agriyield.userservice.application.port.incoming.AdminServicePort;
import com.agriyield.userservice.application.port.outgoing.*;
import com.agriyield.userservice.domain.enums.KycStatus;
import com.agriyield.userservice.domain.exception.BusinessException;
import com.agriyield.userservice.domain.exception.ResourceNotFoundException;
import com.agriyield.userservice.domain.model.User;
import com.agriyield.userservice.infrastructure.adapter.incoming.messaging.UserEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminServicePort {

    private final UserRepositoryPort userRepository;
    private final CachePort cachePort;
    private final UserEventPublisher eventPublisher;

    // US-09 — List all users
    @Override
    public Page<User> getAllUsers(String role, String status,
                                  Pageable pageable) {
        return userRepository.findAllWithFilters(role, status, pageable);
    }

    // US-09 — Get user by ID
    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User", "id", userId));
    }

    // Platform stats
    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countTotalUsers());
        stats.put("totalFarmers", userRepository.countByRole("FARMER"));
        stats.put("totalInvestors", userRepository.countByRole("INVESTOR"));
        stats.put("totalMerchants", userRepository.countByRole("MERCHANT"));
        stats.put("totalOffTakers", userRepository.countByRole("OFF_TAKER"));
        stats.put("pendingKyc", userRepository.countByKycStatus("PENDING"));
        stats.put("verifiedKyc", userRepository.countByKycStatus("VERIFIED"));
        stats.put("rejectedKyc", userRepository.countByKycStatus("REJECTED"));
        stats.put("activeUsers",
            userRepository.countByAccountStatus("ACTIVE"));
        stats.put("suspendedUsers",
            userRepository.countByAccountStatus("SUSPENDED"));
        return stats;
    }

    // Update KYC status
    @Override
    @Transactional
    public User updateKycStatus(UUID userId, String kycStatus,
                                 UUID adminId) {
        log.info("Admin {} updating KYC for user {} to {}",
            adminId, userId, kycStatus);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User", "id", userId));

        KycStatus status = KycStatus.valueOf(kycStatus);
        user.setKycStatus(status);
        if (status == KycStatus.VERIFIED) {
            user.setFaydaVerifiedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        cachePort.delete("user:" + userId);

        if (status == KycStatus.VERIFIED) {
            eventPublisher.publishUserKycVerified(saved);
        }

        return saved;
    }

    // US-10 — Suspend user
    @Override
    @Transactional
    public User suspendUser(UUID userId, UUID adminId, String reason) {
        log.info("Admin {} suspending user {}: {}", adminId, userId, reason);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User", "id", userId));

        if (user.getAccountStatus() ==
                com.agriyield.userservice.domain.enums.AccountStatus.SUSPENDED) {
            throw new BusinessException(
                "User is already suspended", "ALREADY_SUSPENDED");
        }

        user.suspend();
        User saved = userRepository.save(user);
        cachePort.delete("user:" + userId);

        eventPublisher.publishUserSuspended(saved, adminId,
            reason != null ? reason : "Admin action");

        return saved;
    }

    // US-10 — Reactivate user
    @Override
    @Transactional
    public User reactivateUser(UUID userId, UUID adminId) {
        log.info("Admin {} reactivating user {}", adminId, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "User", "id", userId));

        user.activate();
        User saved = userRepository.save(user);
        cachePort.delete("user:" + userId);

        eventPublisher.publishUserReactivated(saved, adminId);

        return saved;
    }
}
