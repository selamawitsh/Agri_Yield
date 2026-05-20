package com.agriyield.userservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.userservice.core.domain.model.BankAccount;
import com.agriyield.userservice.core.port.outgoing.BankAccountRepositoryPort;
import com.agriyield.userservice.infrastructure.adapter.outgoing.persistence.entity.BankAccountEntity;
import com.agriyield.userservice.infrastructure.repository.JpaBankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BankAccountRepositoryAdapter implements BankAccountRepositoryPort {
    
    private final JpaBankAccountRepository jpaRepository;
    private final BankAccountMapper mapper = new BankAccountMapper();
    
    @Override
    public BankAccount save(BankAccount account) {
        BankAccountEntity entity = mapper.toEntity(account);
        BankAccountEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<BankAccount> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
    
    @Override
    public List<BankAccount> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<BankAccount> findByUserIdAndAccountType(UUID userId, String accountType) {
        return jpaRepository.findByUserIdAndAccountType(userId, accountType)
            .map(mapper::toDomain);
    }
    
    @Override
    public Optional<BankAccount> findDefaultByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndIsDefaultTrue(userId)
            .map(mapper::toDomain);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void clearDefaultFlag(UUID userId) {
        jpaRepository.clearDefaultFlag(userId);
    }
    
    @Override
    public long countVerifiedAccounts(UUID userId) {
        return jpaRepository.countByUserIdAndIsVerifiedTrue(userId);
    }
    
    // Inner mapper class
    static class BankAccountMapper {
        BankAccountEntity toEntity(BankAccount domain) {
            if (domain == null) return null;
            return BankAccountEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .accountType(domain.getAccountType())
                .accountNumber(domain.getAccountNumber())
                .accountHolderName(domain.getAccountHolderName())
                .isVerified(domain.isVerified())
                .verifiedAt(domain.getVerifiedAt())
                .isDefault(domain.isDefault())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
        }
        
        BankAccount toDomain(BankAccountEntity entity) {
            if (entity == null) return null;
            return BankAccount.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .accountType(entity.getAccountType())
                .accountNumber(entity.getAccountNumber())
                .accountHolderName(entity.getAccountHolderName())
                .isVerified(entity.isVerified())
                .verifiedAt(entity.getVerifiedAt())
                .isDefault(entity.isDefault())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
        }
    }
}
