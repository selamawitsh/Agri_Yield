package com.agriyield.userservice.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {

    private UUID id;
    private String phone;
    private String email;
    private String faydaId;
    private String role;
    private String kycStatus;
    private String accountStatus;
    private String preferredLanguage;
    private String riskTolerance;
    private String investmentGoal;
    private Integer agriScore;
    private LocalDateTime createdAt;
    private List<BankAccountResponse> bankAccounts;
    private BankAccountResponse defaultBankAccount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BankAccountResponse {
        private UUID id;
        private String accountType;
        private String accountNumber;
        private String accountHolderName;
        private Boolean isVerified;
        private Boolean isDefault;
        private LocalDateTime verifiedAt;
        private LocalDateTime createdAt;
    }
}
