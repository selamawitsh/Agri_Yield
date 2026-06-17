package com.agriyield.offtakerservice.application.port.outgoing;

import java.util.Map;

public interface UserServicePort {
    Map<String, Object> getUserById(String userId);
    Map<String, Object> getFarmerProfile(String userId);
}
