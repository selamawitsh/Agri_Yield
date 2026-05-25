package com.agriyield.userservice.presentation.controller;

import com.agriyield.userservice.application.port.incoming.AdminServicePort;
import com.agriyield.userservice.domain.model.User;
import com.agriyield.userservice.presentation.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminServicePort adminService;

    // US-09 — List all users
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("GET /admin/users — role: {}, status: {}", role, status);
        Page<User> users = adminService.getAllUsers(role, status,
            PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(sortDir), sortBy)));
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    // Platform stats
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        log.info("GET /admin/stats");
        return ResponseEntity.ok(
            ApiResponse.success(adminService.getStats()));
    }

    // US-09 — Get user by ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @PathVariable UUID userId) {
        log.info("GET /admin/users/{}", userId);
        return ResponseEntity.ok(
            ApiResponse.success(adminService.getUserById(userId)));
    }

    // Update KYC status
    @PatchMapping("/users/{userId}/kyc")
    public ResponseEntity<ApiResponse<User>> updateKycStatus(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Id",
                           required = false) UUID adminId) {
        log.info("PATCH /admin/users/{}/kyc — {}", userId,
            body.get("kycStatus"));
        User updated = adminService.updateKycStatus(
            userId, body.get("kycStatus"),
            adminId != null ? adminId : UUID.randomUUID());
        return ResponseEntity.ok(ApiResponse.success(
            "KYC status updated", updated));
    }

    // US-10 — Suspend or reactivate user
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<User>> updateStatus(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> body,
            @RequestHeader(value = "X-User-Id",
                           required = false) UUID adminId) {
        String newStatus = body.get("status");
        String reason = body.get("reason");
        UUID resolvedAdminId = adminId != null
            ? adminId : UUID.randomUUID();

        log.info("PATCH /admin/users/{}/status — {}", userId, newStatus);

        User updated;
        if ("SUSPENDED".equals(newStatus)) {
            updated = adminService.suspendUser(
                userId, resolvedAdminId, reason);
        } else if ("ACTIVE".equals(newStatus)) {
            updated = adminService.reactivateUser(
                userId, resolvedAdminId);
        } else {
            throw new com.agriyield.userservice.domain.exception
                .BusinessException(
                "Invalid status: " + newStatus, "INVALID_STATUS");
        }

        return ResponseEntity.ok(ApiResponse.success(
            "User status updated to " + newStatus, updated));
    }
}
