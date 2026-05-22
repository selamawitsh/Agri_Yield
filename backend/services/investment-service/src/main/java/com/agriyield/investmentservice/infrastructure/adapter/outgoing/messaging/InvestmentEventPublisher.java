package com.agriyield.investmentservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.investmentservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishInvestmentPlaced(Investment investment) {
        Map<String, Object> event = buildBase("investment.placed", investment);
        log.info("Publishing investment.placed: {}", investment.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INVESTMENT_EXCHANGE,
            RabbitMQConfig.INVESTMENT_PLACED_KEY, event);
    }

    @Override
    public void publishInvestmentEscrowLocked(Investment investment) {
        Map<String, Object> event = buildBase("investment.escrow.locked", investment);
        log.info("Publishing investment.escrow.locked: {}", investment.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INVESTMENT_EXCHANGE,
            RabbitMQConfig.INVESTMENT_ESCROW_LOCKED_KEY, event);
    }

    @Override
    public void publishInvestmentCancelled(Investment investment) {
        Map<String, Object> event = buildBase("investment.cancelled", investment);
        event.put("cancelled_reason", investment.getCancelledReason());
        log.info("Publishing investment.cancelled: {}", investment.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INVESTMENT_EXCHANGE,
            RabbitMQConfig.INVESTMENT_CANCELLED_KEY, event);
    }

    @Override
    public void publishInvestmentCompleted(Investment investment) {
        Map<String, Object> event = buildBase("investment.completed", investment);
        event.put("actual_return_pct", investment.getActualReturnPct());
        log.info("Publishing investment.completed: {}", investment.getId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.INVESTMENT_EXCHANGE,
            RabbitMQConfig.INVESTMENT_COMPLETED_KEY, event);
    }

    private Map<String, Object> buildBase(String eventType, Investment investment) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", eventType);
        event.put("investment_id", investment.getId().toString());
        event.put("investor_id", investment.getInvestorId().toString());
        event.put("farm_id", investment.getFarmId().toString());
        event.put("farmer_id", investment.getFarmerId().toString());
        event.put("input_need_id", investment.getInputNeedId().toString());
        event.put("crop_cycle_id", investment.getCropCycleId().toString());
        event.put("amount_etb", investment.getAmountEtb());
        event.put("status", investment.getStatus().getValue());
        event.put("crop_type", investment.getCropType());
        event.put("region", investment.getRegion());
        event.put("season_name", investment.getSeasonName());
        event.put("timestamp", LocalDateTime.now().toString());
        return event;
    }
}
