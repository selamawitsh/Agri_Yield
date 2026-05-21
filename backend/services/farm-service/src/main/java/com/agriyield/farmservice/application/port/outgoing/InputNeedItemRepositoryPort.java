package com.agriyield.farmservice.application.port.outgoing;

import com.agriyield.farmservice.domain.model.InputNeedItem;

import java.util.List;
import java.util.UUID;

public interface InputNeedItemRepositoryPort {

    InputNeedItem save(InputNeedItem item);

    List<InputNeedItem> saveAll(List<InputNeedItem> items);

    List<InputNeedItem> findAllByInputNeedId(UUID inputNeedId);
}
