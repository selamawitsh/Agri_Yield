package com.agriyield.investmentservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.investmentservice.application.port.outgoing.FarmListingRepositoryPort;
import com.agriyield.investmentservice.application.port.outgoing.InvestmentRepositoryPort;
import com.agriyield.investmentservice.application.port.outgoing.PayoutRecordRepositoryPort;
import com.agriyield.investmentservice.domain.enums.InvestmentStatus;
import com.agriyield.investmentservice.domain.enums.ListingStatus;
import com.agriyield.investmentservice.domain.model.FarmListing;
import com.agriyield.investmentservice.domain.model.Investment;
import com.agriyield.investmentservice.domain.model.PayoutRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SRS §4.1 — investment-service consumes:
 *   offtaker.exchange   → bid.accepted         (listing FULLY_FUNDED → ACTIVE)
 *   investment.exchange → settlement.completed  (investments → COMPLETED + payout records)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OfftakerSettlementEventListener {

    private final FarmListingRepositoryPort  listingRepository;
    private final InvestmentRepositoryPort   investmentRepository;
    private final PayoutRecordRepositoryPort payoutRepository;

    // ── bid.accepted: FULLY_FUNDED → ACTIVE ──────────────────────────────────
    @RabbitListener(bindings = @QueueBinding(
            value    = @Queue(value = "investment.bid-accepted.queue", durable = "true"),
            exchange = @Exchange(value = "offtaker.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
            key      = "bid.accepted"
    ))
    @Transactional
    public void onBidAccepted(Map<String, Object> event) {
        log.info("IS: received bid.accepted event: {}", event);
        try {
            String farmIdStr = (String) event.get("farm_id");
            if (farmIdStr == null) return;
            UUID farmId = UUID.fromString(farmIdStr);

            List<FarmListing> listings = listingRepository.findAllOpen();
            for (FarmListing listing : listings) {
                if (!listing.getFarmId().equals(farmId)) continue;
                if (listing.getStatus() != ListingStatus.FULLY_FUNDED) continue;

                listing.activate();
                listingRepository.save(listing);
                log.info("IS: listing {} → ACTIVE after bid.accepted farm={}", listing.getId(), farmId);

                // FIX: was findByInvestorId(farmId) — wrong method, wrong argument.
                // Must query by farmId to find all investments for this farm.
                List<Investment> investments = investmentRepository.findAllByFarmId(farmId);
                for (Investment inv : investments) {
                    if (inv.getStatus() == InvestmentStatus.ESCROW_LOCKED) {
                        inv.activate();
                        investmentRepository.save(inv);
                        log.info("IS: investment {} → ACTIVE farm={}", inv.getId(), farmId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("IS: failed processing bid.accepted: {}", e.getMessage(), e);
        }
    }

    // ── settlement.completed: investments → COMPLETED + payout records ────────
    @RabbitListener(bindings = @QueueBinding(
            value    = @Queue(value = "investment.settlement-completed.queue", durable = "true"),
            exchange = @Exchange(value = "investment.exchange", type = ExchangeTypes.TOPIC, durable = "true"),
            key      = "settlement.completed"
    ))
    @Transactional
    public void onSettlementCompleted(Map<String, Object> event) {
        log.info("IS: received settlement.completed event: {}", event);
        try {
            String farmIdStr    = (String) event.get("farm_id");
            String listingIdStr = (String) event.get("listing_id");
            if (farmIdStr == null || listingIdStr == null) return;

            UUID farmId    = UUID.fromString(farmIdStr);
            UUID listingId = UUID.fromString(listingIdStr);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> investorPayouts =
                    (List<Map<String, Object>>) event.getOrDefault("investor_payouts", List.of());

            for (Map<String, Object> payout : investorPayouts) {
                String investorIdStr = (String) payout.get("investor_id");
                if (investorIdStr == null) continue;

                UUID       investorId = UUID.fromString(investorIdStr);
                BigDecimal principal  = new BigDecimal(payout.get("principal_etb").toString());
                BigDecimal profit     = new BigDecimal(payout.get("profit_etb").toString());
                BigDecimal total      = new BigDecimal(payout.get("total_etb").toString());

                List<Investment> investments = investmentRepository.findByInvestorId(investorId);
                for (Investment inv : investments) {
                    if (!inv.getFarmId().equals(farmId)) continue;
                    if (inv.getStatus() != InvestmentStatus.ACTIVE) continue;

                    BigDecimal actualApr = inv.getAmountEtb().compareTo(BigDecimal.ZERO) > 0
                            ? profit.divide(inv.getAmountEtb(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;

                    inv.complete(actualApr, total);
                    investmentRepository.save(inv);

                    PayoutRecord record = PayoutRecord.builder()
                            .id(UUID.randomUUID())
                            .investmentId(inv.getId())
                            .investorId(inv.getInvestorId())
                            .farmId(inv.getFarmId())
                            .listingId(listingId)
                            .principalEtb(principal)
                            .returnEtb(profit)
                            .totalEtb(total)
                            .actualApr(actualApr)
                            .payoutReason("Harvest settlement")
                            .paidAt(LocalDateTime.now())
                            .build();
                    payoutRepository.save(record);

                    log.info("IS: investment {} COMPLETED investor={} payout={} ETB",
                            inv.getId(), investorId, total);
                    break; // one active investment per investor per farm
                }
            }

            // Mark listing COMPLETED
            listingRepository.findById(listingId).ifPresent(l -> {
                l.complete();
                listingRepository.save(l);
            });

        } catch (Exception e) {
            log.error("IS: failed processing settlement.completed: {}", e.getMessage(), e);
        }
    }
}