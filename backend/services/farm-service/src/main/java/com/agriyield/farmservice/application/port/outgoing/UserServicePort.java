package com.agriyield.farmservice.application.port.outgoing;

import java.util.UUID;

public interface UserServicePort {

    // Verify the farmer exists and is active before registering a farm
    boolean verifyFarmerExists(UUID farmerId);

    // Update agri-score back on user-service after season completion
    void updateAgriScore(UUID farmerId, int score, UUID cropCycleId);
}
