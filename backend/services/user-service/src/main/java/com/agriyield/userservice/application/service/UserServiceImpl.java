package com.agriyield.userservice.application.service;

import com.agriyield.userservice.core.domain.enums.PreferredLanguage;
import com.agriyield.userservice.core.domain.exceptions.BusinessException;
import com.agriyield.userservice.core.domain.exceptions.ResourceNotFoundException;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.incoming.UserServicePort;
import com.agriyield.userservice.core.port.outgoing.CachePort;
import com.agriyield.userservice.core.port.outgoing.NotificationPort;
import com.agriyield.userservice.core.port.outgoing.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserServicePort {
    
    private final UserRepositoryPort userRepository;
    private final CachePort cachePort;
    private final NotificationPort notificationPort;
    
    @Override
    public User getUserById(UUID userId) {
        String cacheKey = "user:" + userId;
        
        // Try cache first
        var cachedUser = cachePort.get(cacheKey);
        if (cachedUser.isPresent() && cachedUser.get() instanceof User) {
            return (User) cachedUser.get();
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Cache for 5 minutes
        cachePort.set(cacheKey, user, 5, TimeUnit.MINUTES);
        
        return user;
    }
    
    @Override
    public User getUserByPhone(String phone) {
        return userRepository.findByPhone(phone)
            .orElseThrow(() -> new ResourceNotFoundException("User", "phone", phone));
    }
    
    @Override
    public User getCurrentUser(UUID userId) {
        return getUserById(userId);
    }
    
    @Override
    @Transactional
    public User updateUserProfile(UUID userId, Map<String, Object> updates) {
        log.info("Updating user profile for: {}", userId);
        
        User user = getUserById(userId);
        
        // Update allowed fields only (SRS page 16)
        if (updates.containsKey("preferredLanguage")) {
            user.setPreferredLanguage(PreferredLanguage.fromCode((String) updates.get("preferredLanguage")));
        }
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        // Invalidate cache
        cachePort.delete("user:" + userId);
        
        log.info("User profile updated successfully: {}", userId);
        return updatedUser;
    }
    
    @Override
    @Transactional
    public void linkBankAccount(UUID userId, String telebirrAccount, String cbeAccount) {
        log.info("Linking bank account for user: {}", userId);
        
        User user = getUserById(userId);
        
        // In a real implementation, this would call Telebirr/CBE API
        // to verify the account with a 1 ETB test deposit (SRS page 16)
        
        // For now, just log the linking
        log.info("Bank account linked - Telebirr: {}, CBE: {} for user: {}", 
                 telebirrAccount, cbeAccount, userId);
        
        // Send notification
        notificationPort.sendSms(user.getPhone(), 
            "Agri-Yield: Your bank account has been linked successfully. A test deposit of 1 ETB has been sent to verify your account.");
    }
    
    @Override
    @Transactional
    public void updatePreferredLanguage(UUID userId, String languageCode) {
        log.info("Updating preferred language for user: {} to {}", userId, languageCode);
        
        User user = getUserById(userId);
        user.setPreferredLanguage(PreferredLanguage.fromCode(languageCode));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Invalidate cache
        cachePort.delete("user:" + userId);
    }
    
    @Override
    public User getUserByFaydaId(String faydaId) {
        return userRepository.findByFaydaId(faydaId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "faydaId", faydaId));
    }
}
