package com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.farmservice.application.port.outgoing.InputNeedItemRepositoryPort;
import com.agriyield.farmservice.domain.model.InputNeedItem;
import com.agriyield.farmservice.infrastructure.adapter.outgoing.persistence.mapper.FarmEntityMapper;
import com.agriyield.farmservice.infrastructure.repository.JpaInputNeedItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InputNeedItemRepositoryAdapter implements InputNeedItemRepositoryPort {

    private final JpaInputNeedItemRepository jpaInputNeedItemRepository;
    private final FarmEntityMapper mapper;

    @Override
    public InputNeedItem save(InputNeedItem item) {
        return mapper.toDomain(jpaInputNeedItemRepository.save(mapper.toEntity(item)));
    }

    @Override
    public List<InputNeedItem> saveAll(List<InputNeedItem> items) {
        return jpaInputNeedItemRepository.saveAll(
            items.stream().map(mapper::toEntity).collect(Collectors.toList()))
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<InputNeedItem> findAllByInputNeedId(UUID inputNeedId) {
        return jpaInputNeedItemRepository.findByInputNeedId(inputNeedId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
