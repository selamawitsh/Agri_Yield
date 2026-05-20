package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.core.domain.model.User;
import com.agriyield.userservice.core.port.outgoing.UserRepositoryPort;
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
        var entity = mapper.toEntity(user);
        var savedEntity = jpaUserRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaUserRepository.findByPhone(phone)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByFaydaId(String faydaId) {
        return jpaUserRepository.findByFaydaId(faydaId)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
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
    public void deleteById(UUID id) {
        jpaUserRepository.deleteById(id);
    }
    
    @Override
    public Page<User> findAllWithFilters(String role, String status, Pageable pageable) {
        if (role != null && !role.equals("ALL") && status != null && !status.equals("ALL")) {
            return jpaUserRepository.findByRoleAndAccountStatus(role, status, pageable)
                .map(mapper::toDomain);
        } else if (role != null && !role.equals("ALL")) {
            return jpaUserRepository.findByRole(role, pageable)
                .map(mapper::toDomain);
        } else if (status != null && !status.equals("ALL")) {
            return jpaUserRepository.findByAccountStatus(status, pageable)
                .map(mapper::toDomain);
        } else {
            return jpaUserRepository.findAll(pageable)
                .map(mapper::toDomain);
        }
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
