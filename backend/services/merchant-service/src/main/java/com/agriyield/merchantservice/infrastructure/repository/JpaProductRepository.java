package com.agriyield.merchantservice.infrastructure.repository;

import com.agriyield.merchantservice.infrastructure.adapter.outgoing.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaProductRepository
        extends JpaRepository<ProductEntity, UUID> {

    List<ProductEntity> findByMerchantId(
            UUID merchantId
    );

    List<ProductEntity> findByMerchantIdAndProductCategory(
            UUID merchantId,
            String productCategory
    );

    List<ProductEntity>
    findByProductCategoryAndMerchantIdInAndIsAvailableTrue(
            String productCategory,
            List<UUID> merchantIds
    );
}