package com.agriyield.userservice.core.port.outgoing;

import java.util.UUID;

public interface InvestorProfileRepositoryPort {
    void createDefaultProfile(UUID userId);
    void updateRiskTolerance(UUID userId, String riskTolerance);
    void updateInvestmentGoal(UUID userId, String investmentGoal);
}
