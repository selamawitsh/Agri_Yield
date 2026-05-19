package com.agriyield.userservice.infrastructure.adapter.incoming.rest;

import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.incoming.AuthServicePort;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.request.LoginRequest;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.request.OtpVerifyRequest;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.request.RegisterRequest;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.response.ApiResponse;
import com.agriyield.userservice.infrastructure.adapter.incoming.rest.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthServicePort authService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST: Registration request for phone: {}", request.getPhone());
        
        User user = authService.register(
            request.getPhone(),
            request.getFaydaId(),
            request.getPassword(),
            request.getRole(),
            request.getFullName()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("OTP sent to your phone. Please verify to complete registration.", 
                   user.getId().toString()));
    }
    
    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        log.info("REST: OTP verification for phone: {}, purpose: {}", request.getPhone(), request.getPurpose());
        
        String result = authService.verifyOtp(request.getPhone(), request.getOtpCode(), request.getPurpose());
        
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", result));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("REST: Login request for phone: {}", request.getPhone());
        
        String accessToken = authService.login(request.getPhone(), request.getPassword());
        
        AuthResponse response = AuthResponse.builder()
            .accessToken(accessToken)
            .expiresIn(86400000L)
            .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {
        log.info("REST: Refresh token request");
        
        String newAccessToken = authService.refreshToken(refreshToken);
        
        AuthResponse response = AuthResponse.builder()
            .accessToken(newAccessToken)
            .expiresIn(86400000L)
            .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {
        log.info("REST: Logout request");
        
        String accessToken = authorization.replace("Bearer ", "");
        authService.logout(accessToken, refreshToken);
        
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
