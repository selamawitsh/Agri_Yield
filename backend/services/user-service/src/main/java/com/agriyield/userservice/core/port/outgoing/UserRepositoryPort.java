package com.agriyield.userservice.core.port.outgoing;

import com.agriyield.userservice.core.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByPhone(String phone);
    Optional<User> findByFaydaId(String faydaId);
    Optional<User> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByFaydaId(String faydaId);
    void deleteById(UUID id);
    
    // Admin methods
    Page<User> findAllWithFilters(String role, String status, Pageable pageable);
    long countTotalUsers();
    long countByRole(String role);
    long countByKycStatus(String kycStatus);
    long countByAccountStatus(String accountStatus);
}
