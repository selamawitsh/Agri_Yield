package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.application.port.outgoing.BankAccountRepositoryPort;
import com.agriyield.userservice.domain.model.BankAccount;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.mapper.EntityDomainMapper;
import com.agriyield.userservice.infrastructure.repository.JpaBankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BankAccountRepositoryAdapter
        implements BankAccountRepositoryPort {

    private final JpaBankAccountRepository jpaRepo;
    private final EntityDomainMapper mapper;

    @Override
    public BankAccount save(BankAccount account) {
        return mapper.toDomain(jpaRepo.save(mapper.toEntity(account)));
    }

    @Override
    public Optional<BankAccount> findById(UUID id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<BankAccount> findByUserId(UUID userId) {
        return jpaRepo.findByUserId(userId).stream()
            .map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<BankAccount> findDefaultByUserId(UUID userId) {
        return jpaRepo.findByUserIdAndIsDefaultTrue(userId)
            .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepo.deleteById(id);
    }

    @Override
    public void clearDefaultForUser(UUID userId) {
        jpaRepo.clearDefaultForUser(userId);
    }
}
