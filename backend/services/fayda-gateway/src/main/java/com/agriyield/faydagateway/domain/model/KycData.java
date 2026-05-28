package com.agriyield.faydagateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycData {
    private String faydaId;
    private String fullName;
    private String dateOfBirth;
    private String region;
    private boolean verified;
}
