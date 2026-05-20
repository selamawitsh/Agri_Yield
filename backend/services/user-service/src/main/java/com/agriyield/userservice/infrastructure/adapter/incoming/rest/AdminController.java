package com.agriyield.userservice.infrastructure.adapter.incoming.rest;

import com.agriyield.userservice.application.service.AdminServiceImpl;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.outgoing.UserRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.incoming.messaging.UserEventPublisher;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepositoryPort userRepository;
    private final AdminServiceImpl adminService;
    private final UserEventPublisher eventPublisher;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Admin fetching users - page: {}, size: {}, role: {}, status: {}", 
                 page, size, role, status);
        
        Pageable pageable = PageRequest.of(page, size, 
            Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        
        Page<User> userPage = userRepository.findAllWithFilters(role, status, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userPage.getContent());
        response.put("currentPage", userPage.getNumber());
        response.put("totalItems", userPage.getTotalElements());
        response.put("totalPages", userPage.getTotalPages());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStats() {
        log.info("Admin fetching user statistics");
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.countTotalUsers());
        stats.put("totalFarmers", userRepository.countByRole("FARMER"));
        stats.put("totalInvestors", userRepository.countByRole("INVESTOR"));
        stats.put("totalMerchants", userRepository.countByRole("MERCHANT"));
        stats.put("totalOffTakers", userRepository.countByRole("OFF_TAKER"));
        stats.put("pendingKyc", userRepository.countByKycStatus("PENDING"));
        stats.put("verifiedKyc", userRepository.countByKycStatus("VERIFIED"));
        stats.put("rejectedKyc", userRepository.countByKycStatus("REJECTED"));
        stats.put("activeUsers", userRepository.countByAccountStatus("ACTIVE"));
        stats.put("suspendedUsers", userRepository.countByAccountStatus("SUSPENDED"));
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable UUID userId) {
        log.info("Admin fetching user details for: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    // NEW: Update KYC Status (SRS Page 58)
    @PatchMapping("/users/{userId}/kyc")
    public ResponseEntity<ApiResponse<User>> updateKycStatus(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-User-Id", required = false) UUID adminId) {
        
        String newKycStatus = request.get("kycStatus");
        String reason = request.get("reason");
        
        log.info("Admin {} updating KYC for user {} to {}", adminId, userId, newKycStatus);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update KYC status (SRS Page 12: PENDING, VERIFIED, REJECTED)
        user.setKycStatus(com.agriyield.userservice.core.domain.enums.KycStatus.valueOf(newKycStatus));
        if ("VERIFIED".equals(newKycStatus)) {
            user.setFaydaVerifiedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        
        User updatedUser = userRepository.save(user);
        
        // Publish user.kyc.verified event (SRS Page 50)
        if ("VERIFIED".equals(newKycStatus)) {
            eventPublisher.publishUserKycVerified(updatedUser);
        }
        
        return ResponseEntity.ok(ApiResponse.success("KYC status updated to " + newKycStatus, updatedUser));
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-User-Id", required = false) UUID adminId) {
        
        String newStatus = request.get("status");
        String reason = request.get("reason");
        log.info("Admin {} updating user {} status to {}", adminId, userId, newStatus);
        
        User updatedUser;
        if ("SUSPENDED".equals(newStatus)) {
            updatedUser = adminService.suspendUser(userId, adminId != null ? adminId : UUID.randomUUID(), reason);
        } else if ("ACTIVE".equals(newStatus)) {
            updatedUser = adminService.reactivateUser(userId, adminId != null ? adminId : UUID.randomUUID());
        } else {
            throw new RuntimeException("Invalid status: " + newStatus);
        }
        
        return ResponseEntity.ok(ApiResponse.success("User status updated", updatedUser));
    }
}
