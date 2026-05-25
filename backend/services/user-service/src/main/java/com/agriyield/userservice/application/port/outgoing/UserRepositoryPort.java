package com.agriyield.userservice.application.port.outgoing;

import com.agriyield.userservice.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UUID userId);

    Optional<User> findByPhone(String phone);

    Optional<User> findByFaydaId(String faydaId);

    boolean existsByPhone(String phone);

    boolean existsByFaydaId(String faydaId);

    Page<User> findAllWithFilters(String role, String status, Pageable pageable);

    long countTotalUsers();

    long countByRole(String role);

    long countByKycStatus(String kycStatus);

    long countByAccountStatus(String accountStatus);
}
