package com.agriyield.userservice.presentation.controller;

import com.agriyield.userservice.application.port.incoming.UserServicePort;
import com.agriyield.userservice.application.port.outgoing.JwtTokenPort;
import com.agriyield.userservice.domain.exception.BusinessException;
import com.agriyield.userservice.domain.model.BankAccount;
import com.agriyield.userservice.domain.model.User;
import com.agriyield.userservice.presentation.dto.request.AddBankAccountRequest;
import com.agriyield.userservice.presentation.dto.request.UpdateProfileRequest;
import com.agriyield.userservice.presentation.dto.response.ApiResponse;
import com.agriyield.userservice.presentation.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServicePort userService;
    private final JwtTokenPort jwtTokenPort;

    private UUID extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(
                "Missing or invalid Authorization header",
                "UNAUTHORIZED");
        }
        var result = jwtTokenPort.validateToken(
            authHeader.substring(7));
        if (!result.isValid()) {
            throw new BusinessException(
                "Invalid or expired token", "UNAUTHORIZED");
        }
        return result.getUserId();
    }

    // US-06 — View own profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = extractUserId(authHeader);
        log.info("GET /users/me — user: {}", userId);

        User user = userService.getCurrentUser(userId);
        List<BankAccount> accounts = userService.getBankAccounts(userId);
        BankAccount defaultAccount = accounts.stream()
            .filter(a -> Boolean.TRUE.equals(a.getIsDefault()))
            .findFirst().orElse(null);

        return ResponseEntity.ok(ApiResponse.success(
            toProfileResponse(user, accounts, defaultAccount)));
    }

    // US-07 — Update profile
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateProfileRequest request) {
        UUID userId = extractUserId(authHeader);
        log.info("PATCH /users/me — user: {}", userId);

        Map<String, Object> updates = new java.util.HashMap<>();
        if (request.getEmail() != null)
            updates.put("email", request.getEmail());
        if (request.getPreferredLanguage() != null)
            updates.put("preferredLanguage", request.getPreferredLanguage());
        if (request.getRiskTolerance() != null)
            updates.put("riskTolerance", request.getRiskTolerance());
        if (request.getInvestmentGoal() != null)
            updates.put("investmentGoal", request.getInvestmentGoal());

        User updated = userService.updateProfile(userId, updates);
        return ResponseEntity.ok(ApiResponse.success(
            "Profile updated successfully",
            toProfileResponse(updated, List.of(), null)));
    }

    // US-08 — Add bank account
    @PostMapping("/me/bank")
    public ResponseEntity<ApiResponse<UserProfileResponse.BankAccountResponse>>
            addBankAccount(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AddBankAccountRequest request) {
        UUID userId = extractUserId(authHeader);
        log.info("POST /users/me/bank — user: {}, type: {}",
            userId, request.getAccountType());

        BankAccount account = userService.addBankAccount(
            userId, request.getAccountType(),
            request.getAccountNumber(), request.getAccountHolderName());

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Bank account added. Verify with the test deposit code.",
                toBankAccountResponse(account)));
    }

    // US-08 — Verify bank account
    @PostMapping("/me/bank/verify")
    public ResponseEntity<ApiResponse<UserProfileResponse.BankAccountResponse>>
            verifyBankAccount(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        UUID userId = extractUserId(authHeader);
        UUID accountId = UUID.fromString(body.get("account_id"));
        String code = body.get("verification_code");

        BankAccount verified = userService.verifyBankAccount(
            userId, accountId, code);

        return ResponseEntity.ok(ApiResponse.success(
            "Bank account verified successfully",
            toBankAccountResponse(verified)));
    }

    // US-08 — Set default
    @PostMapping("/me/bank/default")
    public ResponseEntity<ApiResponse<UserProfileResponse.BankAccountResponse>>
            setDefault(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> body) {
        UUID userId = extractUserId(authHeader);
        UUID accountId = UUID.fromString(body.get("account_id"));

        BankAccount defaultAccount = userService.setDefaultBankAccount(
            userId, accountId);

        return ResponseEntity.ok(ApiResponse.success(
            "Default bank account set",
            toBankAccountResponse(defaultAccount)));
    }

    // US-08 — List bank accounts
    @GetMapping("/me/bank")
    public ResponseEntity<ApiResponse<List<UserProfileResponse.BankAccountResponse>>>
            getBankAccounts(
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = extractUserId(authHeader);
        List<UserProfileResponse.BankAccountResponse> accounts =
            userService.getBankAccounts(userId).stream()
                .map(this::toBankAccountResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    // US-08 — Delete bank account
    @DeleteMapping("/me/bank/{accountId}")
    public ResponseEntity<ApiResponse<String>> deleteBankAccount(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID accountId) {
        UUID userId = extractUserId(authHeader);
        userService.deleteBankAccount(userId, accountId);
        return ResponseEntity.ok(
            ApiResponse.success("Bank account deleted successfully"));
    }

    // US-09 — Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
            @PathVariable UUID id) {
        log.info("GET /users/{}", id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(
            toProfileResponse(user, List.of(), null)));
    }

    // Mappers
    private UserProfileResponse toProfileResponse(User user,
            List<BankAccount> accounts, BankAccount defaultAccount) {
        return UserProfileResponse.builder()
            .id(user.getId())
            .phone(user.getPhone())
            .email(user.getEmail())
            .faydaId(user.getFaydaId())
            .role(user.getRole().getValue())
            .kycStatus(user.getKycStatus().getValue())
            .accountStatus(user.getAccountStatus().getValue())
            .preferredLanguage(user.getPreferredLanguage().getCode())
            .riskTolerance(user.getRiskTolerance())
            .investmentGoal(user.getInvestmentGoal())
            .agriScore(user.getAgriScore())
            .createdAt(user.getCreatedAt())
            .bankAccounts(accounts.stream()
                .map(this::toBankAccountResponse)
                .collect(Collectors.toList()))
            .defaultBankAccount(defaultAccount != null
                ? toBankAccountResponse(defaultAccount) : null)
            .build();
    }

    private UserProfileResponse.BankAccountResponse toBankAccountResponse(
            BankAccount account) {
        return UserProfileResponse.BankAccountResponse.builder()
            .id(account.getId())
            .accountType(account.getAccountType())
            .accountNumber(account.getAccountNumber())
            .accountHolderName(account.getAccountHolderName())
            .isVerified(account.getIsVerified())
            .isDefault(account.getIsDefault())
            .verifiedAt(account.getVerifiedAt())
            .createdAt(account.getCreatedAt())
            .build();
    }
}
