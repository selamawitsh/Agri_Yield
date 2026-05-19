package com.agriyield.userservice.infrastructure.adapter.incoming.rest;

import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.incoming.UserServicePort;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserServicePort userService;
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(
            @RequestHeader("X-User-Id") UUID userId) {
        log.info("REST: Get current user: {}", userId);
        
        User user = userService.getCurrentUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<User>> updateUserProfile(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody Map<String, Object> updates) {
        log.info("REST: Update user profile: {}, updates: {}", userId, updates.keySet());
        
        User updatedUser = userService.updateUserProfile(userId, updates);
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedUser));
    }
    
    @PostMapping("/me/bank")
    public ResponseEntity<ApiResponse<String>> linkBankAccount(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody Map<String, String> bankDetails) {
        log.info("REST: Link bank account for user: {}", userId);
        
        userService.linkBankAccount(
            userId,
            bankDetails.get("telebirr_account"),
            bankDetails.get("cbe_account")
        );
        
        return ResponseEntity.ok(ApiResponse.success("Bank account linked and verification initiated"));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        log.info("REST: Get user by ID: {}", id);
        
        User user = userService.getUserById(id);
        
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
