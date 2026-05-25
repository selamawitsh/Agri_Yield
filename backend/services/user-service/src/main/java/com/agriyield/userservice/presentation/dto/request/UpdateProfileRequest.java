package com.agriyield.userservice.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateProfileRequest {
    private String email;
    private String preferredLanguage;
    private String riskTolerance;
    private String investmentGoal;
}
