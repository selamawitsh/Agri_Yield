package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(name = "fayda_id", nullable = false, unique = true, length = 50)
    private String faydaId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "kyc_status", nullable = false, length = 20)
    private String kycStatus;

    @Column(name = "account_status", nullable = false, length = 20)
    private String accountStatus;

    @Column(name = "fayda_verified_at")
    private LocalDateTime faydaVerifiedAt;

    @Column(name = "preferred_language", nullable = false, length = 10)
    private String preferredLanguage;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "risk_tolerance", length = 20)
    private String riskTolerance;

    @Column(name = "investment_goal")
    private String investmentGoal;

    @Column(name = "agri_score")
    private Integer agriScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
