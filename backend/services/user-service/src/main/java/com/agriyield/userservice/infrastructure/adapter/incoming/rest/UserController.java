package com.agriyield.userservice.infrastructure.adapter.incoming.rest;

import com.agriyield.userservice.application.service.BankAccountService;
import com.agriyield.userservice.core.domain.model.BankAccount;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.incoming.UserServicePort;
import com.agriyield.userservice.core.port.outgoing.JwtTokenPort;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserServicePort userService;
    private final JwtTokenPort jwtTokenPort;
    private final BankAccountService bankAccountService;
    
    private UUID getUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        String token = authorizationHeader.substring(7);
        var result = jwtTokenPort.validateToken(token);
        if (!result.isValid()) {
            throw new RuntimeException("Invalid token: " + result.getError());
        }
        return result.getUserId();
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(
            @RequestHeader("Authorization") String authorization) {
        
        UUID userId = getUserIdFromToken(authorization);
        log.info("GET /me for user: {}", userId);
        
        User user = userService.getCurrentUser(userId);
        List<BankAccount> bankAccounts = bankAccountService.getUserBankAccounts(userId);
        BankAccount defaultAccount = bankAccountService.getDefaultBankAccount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("phone", user.getPhone());
        response.put("email", user.getEmail());
        response.put("faydaId", user.getFaydaId());
        response.put("role", user.getRole());
        response.put("kycStatus", user.getKycStatus());
        response.put("accountStatus", user.getAccountStatus());
        response.put("preferredLanguage", user.getPreferredLanguage());
        response.put("riskTolerance", user.getRiskTolerance());
        response.put("investmentGoal", user.getInvestmentGoal());
        response.put("agriScore", user.getAgriScore());
        response.put("createdAt", user.getCreatedAt());
        response.put("bankAccounts", bankAccounts);
        response.put("defaultBankAccount", defaultAccount);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUserProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> updates) {
        
        UUID userId = getUserIdFromToken(authorization);
        log.info("PATCH /me for user: {}, updates: {}", userId, updates.keySet());
        
        User updatedUser = userService.updateUserProfile(userId, updates);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", updatedUser.getId());
        response.put("phone", updatedUser.getPhone());
        response.put("email", updatedUser.getEmail());
        response.put("role", updatedUser.getRole());
        response.put("kycStatus", updatedUser.getKycStatus());
        response.put("riskTolerance", updatedUser.getRiskTolerance());
        response.put("investmentGoal", updatedUser.getInvestmentGoal());
        
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }
    
    // NEW: Add bank account (SRS Page 16)
    @PostMapping("/me/bank")
    public ResponseEntity<ApiResponse<BankAccount>> addBankAccount(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> request) {
        
        UUID userId = getUserIdFromToken(authorization);
        String accountType = request.get("account_type");
        String accountNumber = request.get("account_number");
        String accountHolderName = request.get("account_holder_name");
        
        log.info("POST /me/bank for user: {}, type: {}", userId, accountType);
        
        BankAccount account = bankAccountService.addBankAccount(userId, accountType, accountNumber, accountHolderName);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Bank account added. Please verify with the test deposit code.", account));
    }
    
    // NEW: Verify bank account with test deposit code
    @PostMapping("/me/bank/verify")
    public ResponseEntity<ApiResponse<BankAccount>> verifyBankAccount(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> request) {
        
        UUID userId = getUserIdFromToken(authorization);
        UUID accountId = UUID.fromString(request.get("account_id"));
        String verificationCode = request.get("verification_code");
        
        log.info("POST /me/bank/verify for user: {}, account: {}", userId, accountId);
        
        BankAccount verified = bankAccountService.verifyBankAccount(accountId, verificationCode);
        
        return ResponseEntity.ok(ApiResponse.success("Bank account verified successfully", verified));
    }
    
    // NEW: Set default bank account
    @PostMapping("/me/bank/default")
    public ResponseEntity<ApiResponse<BankAccount>> setDefaultBankAccount(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> request) {
        
        UUID userId = getUserIdFromToken(authorization);
        UUID accountId = UUID.fromString(request.get("account_id"));
        
        log.info("POST /me/bank/default for user: {}, account: {}", userId, accountId);
        
        BankAccount defaultAccount = bankAccountService.setDefaultAccount(userId, accountId);
        
        return ResponseEntity.ok(ApiResponse.success("Default bank account set", defaultAccount));
    }
    
    // NEW: Get all bank accounts
    @GetMapping("/me/bank")
    public ResponseEntity<ApiResponse<List<BankAccount>>> getBankAccounts(
            @RequestHeader("Authorization") String authorization) {
        
        UUID userId = getUserIdFromToken(authorization);
        log.info("GET /me/bank for user: {}", userId);
        
        List<BankAccount> accounts = bankAccountService.getUserBankAccounts(userId);
        
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }
    
    // NEW: Delete bank account
    @DeleteMapping("/me/bank/{accountId}")
    public ResponseEntity<ApiResponse<Void>> deleteBankAccount(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID accountId) {
        
        UUID userId = getUserIdFromToken(authorization);
        log.info("DELETE /me/bank/{} for user: {}", accountId, userId);
        
        bankAccountService.deleteBankAccount(userId, accountId);
        
        return ResponseEntity.ok(ApiResponse.success("Bank account deleted successfully", null));
    }
    
    @PostMapping("/me/bank/link")
    public ResponseEntity<ApiResponse<String>> linkBankAccount(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, String> bankDetails) {
        
        UUID userId = getUserIdFromToken(authorization);
        log.info("POST /me/bank/link for user: {} (deprecated, use /me/bank instead)", userId);
        
        // For backward compatibility
        String accountType = bankDetails.containsKey("telebirr_account") ? "TELEBIRR" : "CBE";
        String accountNumber = bankDetails.getOrDefault("telebirr_account", bankDetails.get("cbe_account"));
        
        bankAccountService.addBankAccount(userId, accountType, accountNumber, null);
        
        return ResponseEntity.ok(ApiResponse.success("Bank account linking initiated. Check SMS for verification code."));
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
