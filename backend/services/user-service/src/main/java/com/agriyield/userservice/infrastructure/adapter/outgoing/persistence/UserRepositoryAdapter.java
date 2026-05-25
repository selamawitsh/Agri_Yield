package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.application.port.outgoing.UserRepositoryPort;
import com.agriyield.userservice.domain.model.User;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.mapper.EntityDomainMapper;
import com.agriyield.userservice.infrastructure.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final EntityDomainMapper mapper;

    @Override
    public User save(User user) {
        return mapper.toDomain(
            jpaUserRepository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return jpaUserRepository.findById(userId).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaUserRepository.findByPhone(phone).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByFaydaId(String faydaId) {
        return jpaUserRepository.findByFaydaId(faydaId)
            .map(mapper::toDomain);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaUserRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByFaydaId(String faydaId) {
        return jpaUserRepository.existsByFaydaId(faydaId);
    }

    @Override
    public Page<User> findAllWithFilters(String role, String status,
                                          Pageable pageable) {
        return jpaUserRepository
            .findAllWithFilters(role, status, pageable)
            .map(mapper::toDomain);
    }

    @Override
    public long countTotalUsers() {
        return jpaUserRepository.count();
    }

    @Override
    public long countByRole(String role) {
        return jpaUserRepository.countByRole(role);
    }

    @Override
    public long countByKycStatus(String kycStatus) {
        return jpaUserRepository.countByKycStatus(kycStatus);
    }

    @Override
    public long countByAccountStatus(String accountStatus) {
        return jpaUserRepository.countByAccountStatus(accountStatus);
    }
}
