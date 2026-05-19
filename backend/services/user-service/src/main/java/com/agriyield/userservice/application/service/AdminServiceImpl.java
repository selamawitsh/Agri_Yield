package com.agriyield.userservice.application.service;

import com.agriyield.userservice.core.domain.enums.AccountStatus;
import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.outgoing.CachePort;
import com.agriyield.userservice.core.port.outgoing.NotificationPort;
import com.agriyield.userservice.core.port.outgoing.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl {
    
    private final UserRepositoryPort userRepository;
    private final CachePort cachePort;
    private final NotificationPort notificationPort;
    
    public User suspendUser(UUID userId, UUID adminId, String reason) {
        log.info("Admin {} suspending user: {} reason: {}", adminId, userId, reason);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.suspend();
        User suspendedUser = userRepository.save(user);
        
        // Invalidate cache
        cachePort.delete("user:" + userId);
        
        // Send notification (SRS page 16: publish user.suspended event)
        notificationPort.sendSms(user.getPhone(), 
            String.format("Agri-Yield: Your account has been suspended. Reason: %s. Contact support for assistance.", reason));
        
        log.info("User suspended successfully: {}", userId);
        return suspendedUser;
    }
    
    public User reactivateUser(UUID userId, UUID adminId) {
        log.info("Admin {} reactivating user: {}", adminId, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.activate();
        User reactivatedUser = userRepository.save(user);
        
        // Invalidate cache
        cachePort.delete("user:" + userId);
        
        // Send notification (SRS page 16: publish user.reactivated event)
        notificationPort.sendSms(user.getPhone(), 
            "Agri-Yield: Your account has been reactivated. You can now log in again.");
        
        log.info("User reactivated successfully: {}", userId);
        return reactivatedUser;
    }
    
    public List<User> getAllUsers(int page, int size) {
        // This would be implemented with pagination from repository
        // For now, return empty list
        return List.of();
    }
    
    public long getTotalUserCount() {
        // This would be implemented from repository
        return 0;
    }
}
