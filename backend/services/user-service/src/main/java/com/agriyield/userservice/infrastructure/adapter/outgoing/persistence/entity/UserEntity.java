package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "phone", nullable = false, unique = true, length = 20)
    private String phone;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "fayda_id", nullable = false, unique = true, length = 50)
    private String faydaId;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "role", nullable = false, length = 20)
    private String role;
    
    @Column(name = "kyc_status", nullable = false, length = 20)
    private String kycStatus;
    
    @Column(name = "account_status", nullable = false, length = 20)
    private String accountStatus;
    
    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage;
    
    @Column(name = "fayda_verified_at")
    private LocalDateTime faydaVerifiedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
