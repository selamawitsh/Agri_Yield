package com.agriyield.userservice.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    private UUID id;
    private UUID userId;
    private String accountType;  // TELEBIRR or CBE
    private String accountNumber;
    private String accountHolderName;
    private boolean isVerified;
    private LocalDateTime verifiedAt;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public void markVerified() {
        this.isVerified = true;
        this.verifiedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
