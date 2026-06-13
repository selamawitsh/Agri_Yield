package com.agriyield.voucherservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.voucherservice.infrastructure.config.InputNeedProductCache;
import com.agriyield.voucherservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Listens to farm.exchange → input.needs.created.
 * Caches the product items (category, name, quantity, sequence_order)
 * so that when listing.fully.funded fires, voucher generation can use
 * the real product details instead of generic "Agricultural Input Package / OTHER".
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InputNeedsEventListener {

    private final InputNeedProductCache productCache;

    @RabbitListener(queues = RabbitMQConfig.INPUT_NEEDS_QUEUE)
    public void onInputNeedsCreated(Map<String, Object> event) {
        log.info("VS: received input.needs.created event for farm={}",
            event.get("farm_id"));
        try {
            String inputNeedId = (String) event.get("input_need_id");
            if (inputNeedId == null) {
                log.warn("VS: input.needs.created missing input_need_id — ignoring");
                return;
            }

            // Items array: [{ category, product_name, amount_etb, sequence_order }]
            Object itemsObj = event.get("items");
            if (!(itemsObj instanceof List<?> rawItems) || rawItems.isEmpty()) {
                log.warn("VS: input.needs.created has no items for input_need_id={}", inputNeedId);
                return;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) rawItems;
            productCache.store(inputNeedId, items);

            log.info("VS: cached {} product items for input_need_id={}", items.size(), inputNeedId);
            items.forEach(item -> log.info("  → category={} product={} amount={}",
                item.get("category"), item.get("product_name"), item.get("amount_etb")));

        } catch (Exception e) {
            log.error("VS: failed processing input.needs.created: {}", e.getMessage(), e);
        }
    }
}
