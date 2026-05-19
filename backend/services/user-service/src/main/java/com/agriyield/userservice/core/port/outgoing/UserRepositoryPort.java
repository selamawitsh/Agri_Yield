package com.agriyield.userservice.core.port.outgoing;

import com.agriyield.userservice.core.domain.model.User;

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
}