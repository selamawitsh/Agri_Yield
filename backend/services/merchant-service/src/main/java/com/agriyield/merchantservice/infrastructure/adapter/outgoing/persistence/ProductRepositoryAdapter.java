package com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.merchantservice.application.port.outgoing.ProductRepositoryPort;
import com.agriyield.merchantservice.domain.enums.ProductCategory;
import com.agriyield.merchantservice.domain.model.Product;
import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.mapper.MerchantEntityMapper;
import com.agriyield.merchantservice.infrastructure.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final JpaProductRepository jpaRepository;
    private final MerchantEntityMapper mapper;

    @Override
    public Product save(Product product) {

        return mapper.toDomain(
                jpaRepository.save(
                        mapper.toEntity(product)
                )
        );
    }

    @Override
    public Optional<Product> findById(UUID id) {

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Product> findByMerchantId(UUID merchantId) {

        return jpaRepository.findByMerchantId(merchantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByMerchantIdAndCategory(
            UUID merchantId,
            ProductCategory category
    ) {

        return jpaRepository
                .findByMerchantIdAndProductCategory(
                        merchantId,
                        category.name()
                )
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByMerchantIdsAndCategory(
            List<UUID> merchantIds,
            String category
    ) {

        return jpaRepository
                .findByProductCategoryAndMerchantIdInAndIsAvailableTrue(
                        category,
                        merchantIds
                )
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}