package com.agriyield.userservice.application.port.incoming;

import com.agriyield.userservice.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface AdminServicePort {

    // US-09 — List all users with filters
    Page<User> getAllUsers(String role, String status, Pageable pageable);

    // US-09 — Get user by ID
    User getUserById(UUID userId);

    // Platform stats
    Map<String, Object> getStats();

    // Update KYC status
    User updateKycStatus(UUID userId, String kycStatus, UUID adminId);

    // US-10 — Suspend user
    User suspendUser(UUID userId, UUID adminId, String reason);

    // US-10 — Reactivate user
    User reactivateUser(UUID userId, UUID adminId);
}
