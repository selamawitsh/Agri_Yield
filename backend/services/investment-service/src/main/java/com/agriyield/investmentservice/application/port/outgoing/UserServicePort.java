package com.agriyield.investmentservice.application.port.outgoing;

import java.util.UUID;

public interface UserServicePort {

    boolean verifyInvestorExists(UUID investorId);
}
