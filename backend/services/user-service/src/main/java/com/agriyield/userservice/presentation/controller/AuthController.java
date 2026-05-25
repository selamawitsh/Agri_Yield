package com.agriyield.userservice.presentation.controller;

import com.agriyield.userservice.application.port.incoming.AuthServicePort;
import com.agriyield.userservice.presentation.dto.request.*;
import com.agriyield.userservice.presentation.dto.response.ApiResponse;
import com.agriyield.userservice.presentation.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServicePort authService;

    // US-01
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {
        log.info("POST /auth/register — phone: {}", request.getPhone());
        var user = authService.register(
            request.getPhone(), request.getFaydaId(),
            request.getPassword(), request.getRole(),
            request.getFullName());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "OTP sent. Please verify to complete registration.",
                user.getId().toString()));
    }

    // US-02
    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody OtpVerifyRequest request) {
        log.info("POST /auth/otp/verify — phone: {}", request.getPhone());
        String result = authService.verifyOtp(
            request.getPhone(), request.getOtpCode(),
            request.getPurpose());
        return ResponseEntity.ok(
            ApiResponse.success("OTP verified successfully", result));
    }

    // US-03
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        log.info("POST /auth/login — phone: {}", request.getPhone());
        Map<String, String> tokens = authService.login(
            request.getPhone(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success(
            AuthResponse.builder()
                .accessToken(tokens.get("accessToken"))
                .refreshToken(tokens.get("refreshToken"))
                .expiresIn(Long.parseLong(tokens.get("expiresIn")))
                .build()));
    }

    // US-04
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        if (refreshToken == null || refreshToken.isBlank()) {
            refreshToken = body.get("refreshToken");
        }
        log.info("POST /auth/refresh");
        String newToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(
            AuthResponse.builder()
                .accessToken(newToken)
                .expiresIn(86400000L)
                .build()));
    }

    // US-05
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader(value = "X-Refresh-Token",
                           required = false) String refreshToken) {
        log.info("POST /auth/logout");
        String accessToken = authorization.replace("Bearer ", "");
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(
            ApiResponse.success("Logged out successfully"));
    }
}
