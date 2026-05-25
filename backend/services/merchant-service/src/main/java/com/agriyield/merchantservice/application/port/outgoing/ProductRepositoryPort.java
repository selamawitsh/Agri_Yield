package com.agriyield.merchantservice.application.port.outgoing;

import com.agriyield.merchantservice.domain.enums.ProductCategory;
import com.agriyield.merchantservice.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findByMerchantId(UUID merchantId);

    List<Product> findByMerchantIdAndCategory(
            UUID merchantId,
            ProductCategory category
    );

    List<Product> findByMerchantIdsAndCategory(
            List<UUID> merchantIds,
            String category
    );

    void deleteById(UUID id);
}