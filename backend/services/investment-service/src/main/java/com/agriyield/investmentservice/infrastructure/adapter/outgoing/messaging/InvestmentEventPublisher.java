package com.agriyield.investmentservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.investmentservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.investmentservice.application.port.outgoing.InvestmentRepositoryPort;
import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final InvestmentRepositoryPort investmentRepository;

    @Override
    public void publishInvestmentPlaced(Investment investment) {
        Map<String, Object> event = buildInvestmentBase("investment.placed", investment);
        rabbitTemplate.convertAndSend(RabbitMQConfig.INVESTMENT_EXCHANGE,
                RabbitMQConfig.INVESTMENT_PLACED_KEY, event);
        log.info("Published investment.placed: {}", investment.getId());
    }

    @Override
    public void publishInvestmentEscrowLocked(Investment investment) {
        Map<String, Object> event = buildInvestmentBase("investment.escrow.locked", investment);
        rabbitTemplate.convertAndSend(RabbitMQConfig.INVESTMENT_EXCHANGE,
                RabbitMQConfig.INVESTMENT_ESCROW_LOCKED_KEY, event);
        log.info("Published investment.escrow.locked: {}", investment.getId());
    }

    @Override
    public void publishInvestmentCancelled(Investment investment) {
        Map<String, Object> event = buildInvestmentBase("investment.cancelled", investment);
        event.put("cancelled_reason", investment.getCancelledReason());
        rabbitTemplate.convertAndSend(RabbitMQConfig.INVESTMENT_EXCHANGE,
                RabbitMQConfig.INVESTMENT_CANCELLED_KEY, event);
        log.info("Published investment.cancelled: {}", investment.getId());
    }

    @Override
    public void publishInvestmentCompleted(Investment investment) {
        Map<String, Object> event = buildInvestmentBase("investment.completed", investment);
        event.put("actual_return_pct", investment.getActualReturnPct());
        rabbitTemplate.convertAndSend(RabbitMQConfig.INVESTMENT_EXCHANGE,
                RabbitMQConfig.INVESTMENT_COMPLETED_KEY, event);
        log.info("Published investment.completed: {}", investment.getId());
    }

    @Override
    public void publishListingCreated(FarmListing listing) {
        Map<String, Object> event = buildListingBase("listing.created", listing);
        rabbitTemplate.convertAndSend(RabbitMQConfig.INVESTMENT_EXCHANGE,
                RabbitMQConfig.LISTING_CREATED_KEY, event);
        log.info("Published listing.created: {}", listing.getId());
    }

    @Override
    public void publishListingFullyFunded(FarmListing listing) {
        // FIX: event_type was "listing.fully.funded" — now aligns with routing key
        // "investment.funded" so voucher-service receives this event correctly.
        Map<String, Object> event = buildListingBase("investment.funded", listing);
        event.put("fully_funded_at", listing.getFullyFundedAt().toString());

        // Include all investors for this listing so voucher-service can act per investment
        List<Map<String, Object>> investments = investmentRepository
                .findAllByFarmId(listing.getFarmId())
                .stream()
                .filter(inv -> inv.getInputNeedId().equals(listing.getInputNeedId()))
                .map(inv -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("investment_id",  inv.getId().toString());
                    m.put("investor_id",    inv.getInvestorId().toString());
                    m.put("amount_etb",     inv.getAmountEtb());
                    m.put("status",         inv.getStatus().getValue());
                    m.put("input_need_id",  inv.getInputNeedId().toString());
                    return m;
                })
                .collect(Collectors.toList());
        event.put("investments", investments);

        // SRS §5.2: payload must include investor_count and total_raised_etb
        event.put("investor_count",    investments.size());
        event.put("total_raised_etb",  listing.getFundedAmountEtb());

        rabbitTemplate.convertAndSend(RabbitMQConfig.INVESTMENT_EXCHANGE,
                RabbitMQConfig.LISTING_FULLY_FUNDED_KEY, event);
        log.info("Published investment.funded: {}", listing.getId());
    }

    @Override
    public void publishListingFundingFailed(FarmListing listing) {
        Map<String, Object> event = buildListingBase("listing.funding.failed", listing);
        event.put("funded_pct", listing.getFundingPct());
        rabbitTemplate.convertAndSend(RabbitMQConfig.INVESTMENT_EXCHANGE,
                RabbitMQConfig.LISTING_FUNDING_FAILED_KEY, event);
        log.info("Published listing.funding.failed: {}", listing.getId());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> buildInvestmentBase(String eventType, Investment i) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type",    eventType);
        event.put("investment_id", i.getId().toString());
        event.put("investor_id",   i.getInvestorId().toString());
        event.put("farm_id",       i.getFarmId().toString());
        event.put("farmer_id",     i.getFarmerId().toString());
        event.put("input_need_id", i.getInputNeedId().toString());
        event.put("crop_cycle_id", i.getCropCycleId().toString());
        event.put("amount_etb",    i.getAmountEtb());
        event.put("status",        i.getStatus().getValue());
        event.put("crop_type",     i.getCropType());
        event.put("region",        i.getRegion());
        event.put("season_name",   i.getSeasonName());
        event.put("timestamp",     LocalDateTime.now().toString());
        return event;
    }

    private Map<String, Object> buildListingBase(String eventType, FarmListing l) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type",        eventType);
        event.put("listing_id",        l.getId().toString());
        event.put("farm_id",           l.getFarmId().toString());
        event.put("farmer_id",         l.getFarmerId().toString());
        event.put("input_need_id",     l.getInputNeedId().toString());
        event.put("crop_cycle_id",     l.getCropCycleId().toString());
        event.put("crop_type",         l.getCropType());
        event.put("region",            l.getRegion());
        event.put("season_name",       l.getSeasonName());
        event.put("total_amount_etb",  l.getTotalAmountEtb());
        event.put("funded_amount_etb", l.getFundedAmountEtb());
        event.put("funding_pct",       l.getFundingPct());
        event.put("current_apr",       l.getCurrentApr());
        event.put("agri_score",        l.getAgriScore());
        event.put("status",            l.getStatus().getValue());
        event.put("timestamp",         LocalDateTime.now().toString());
        return event;
    }
}