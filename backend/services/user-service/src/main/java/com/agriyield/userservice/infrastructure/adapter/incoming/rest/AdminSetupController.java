package com.agriyield.userservice.infrastructure.adapter.incoming.rest;

import com.agriyield.userservice.core.domain.enums.AccountStatus;
import com.agriyield.userservice.core.domain.enums.KycStatus;
import com.agriyield.userservice.core.domain.enums.UserRole;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.outgoing.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/setup")
@RequiredArgsConstructor
public class AdminSetupController {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/reset-admin")
    public ResponseEntity<Map<String, String>> resetAdmin() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Delete existing admin
            userRepository.findByPhone("+251911111111").ifPresent(user -> {
                userRepository.deleteById(user.getId());
            });
            
            // Create new admin with password 'admin123'
            String plainPassword = "admin123";
            String encodedPassword = passwordEncoder.encode(plainPassword);
            
            User admin = User.builder()
                .id(UUID.randomUUID())
                .phone("+251911111111")
                .faydaId("ADMIN001")
                .passwordHash(encodedPassword)
                .role(UserRole.ADMIN)
                .kycStatus(KycStatus.VERIFIED)
                .accountStatus(AccountStatus.ACTIVE)
                .preferredLanguage(com.agriyield.userservice.core.domain.enums.PreferredLanguage.EN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            
            userRepository.save(admin);
            
            response.put("status", "success");
            response.put("message", "Admin created with phone: +251911111111, password: " + plainPassword);
            response.put("phone", "+251911111111");
            response.put("password", plainPassword);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
