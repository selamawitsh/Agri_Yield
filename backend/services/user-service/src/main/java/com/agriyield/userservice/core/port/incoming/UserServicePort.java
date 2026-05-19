package com.agriyield.userservice.core.port.incoming;

import com.agriyield.userservice.core.domain.model.User;

import java.util.Map;
import java.util.UUID;

public interface UserServicePort {

    User getUserById(UUID userId);

    User getUserByPhone(String phone);

    User getCurrentUser(UUID userId);

    User updateUserProfile(UUID userId, Map<String, Object> updates);

    void linkBankAccount(UUID userId, String telebirrAccount, String cbeAccount);

    void updatePreferredLanguage(UUID userId, String languageCode);

    User getUserByFaydaId(String faydaId);
}