package com.agriyield.investmentservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.investmentservice.infrastructure.adapter.outgoing.persistence.entity.FarmJourneyEventEntity;
import com.agriyield.investmentservice.infrastructure.repository.JpaFarmJourneyEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JourneyEventListener {

    private final JpaFarmJourneyEventRepository journeyRepository;

    @RabbitListener(bindings = @QueueBinding(
        value    = @Queue(value = "investment.journey.bid-accepted.queue", durable = "true"),
        exchange = @Exchange(value = "offtaker.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key      = "bid.accepted"
    ))
    public void onBidAccepted(Map<String, Object> event) {
        recordJourneyEvent(event, "BID_ACCEPTED");
    }

    @RabbitListener(bindings = @QueueBinding(
        value    = @Queue(value = "investment.journey.logistics.queue", durable = "true"),
        exchange = @Exchange(value = "offtaker.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key      = "logistics.dispatched"
    ))
    public void onLogisticsDispatched(Map<String, Object> event) {
        recordJourneyEvent(event, "TRUCKS_DISPATCHED");
    }

    @RabbitListener(bindings = @QueueBinding(
        value    = @Queue(value = "investment.journey.harvest.queue", durable = "true"),
        exchange = @Exchange(value = "offtaker.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key      = "harvest.confirmed"
    ))
    public void onHarvestConfirmed(Map<String, Object> event) {
        recordJourneyEvent(event, "HARVEST_CONFIRMED");
    }

    @RabbitListener(bindings = @QueueBinding(
        value    = @Queue(value = "investment.journey.settlement.queue", durable = "true"),
        exchange = @Exchange(value = "investment.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key      = "settlement.completed"
    ))
    public void onSettlementCompleted(Map<String, Object> event) {
        recordJourneyEvent(event, "SETTLEMENT_COMPLETED");
    }

    @RabbitListener(bindings = @QueueBinding(
        value    = @Queue(value = "investment.journey.funded.queue", durable = "true"),
        exchange = @Exchange(value = "investment.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key      = "investment.funded"
    ))
    public void onInvestmentFunded(Map<String, Object> event) {
        recordJourneyEvent(event, "FULLY_FUNDED");
    }

    @RabbitListener(bindings = @QueueBinding(
        value    = @Queue(value = "investment.journey.drought.queue", durable = "true"),
        exchange = @Exchange(value = "weather.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
        key      = "drought.triggered"
    ))
    public void onDroughtTriggered(Map<String, Object> event) {
        recordJourneyEvent(event, "DROUGHT_TRIGGERED");
    }

    private void recordJourneyEvent(Map<String, Object> event, String eventType) {
        try {
            String farmIdStr = (String) event.get("farm_id");
            if (farmIdStr == null) {
                log.warn("JourneyListener: no farm_id in {} event", eventType);
                return;
            }
            FarmJourneyEventEntity entity = FarmJourneyEventEntity.builder()
                    .farmId(UUID.fromString(farmIdStr))
                    .eventType(eventType)
                    .eventData(event)
                    .occurredAt(OffsetDateTime.now())
                    .build();
            journeyRepository.save(entity);
            log.info("Journey event recorded: {} for farm={}", eventType, farmIdStr);
        } catch (Exception e) {
            log.error("Failed to record journey event {}: {}", eventType, e.getMessage());
        }
    }
}
