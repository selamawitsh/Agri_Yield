package com.agriyield.userservice.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {

    private UUID id;
    private UUID userId;
    private String accountType;   // TELEBIRR | CBE
    private String accountNumber;
    private String accountHolderName;
    private Boolean isVerified;
    private Boolean isDefault;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}
