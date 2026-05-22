package com.agriyield.escrowservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.escrowservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.escrowservice.domain.model.EscrowAccount;
import com.agriyield.escrowservice.domain.model.EscrowRelease;
import com.agriyield.escrowservice.infrastructure.config.RabbitMQConfig;
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
public class EscrowEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishEscrowLocked(EscrowAccount escrowAccount) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "escrow.locked");
        event.put("escrow_id", escrowAccount.getId().toString());
        event.put("investment_id", escrowAccount.getInvestmentId().toString());
        event.put("farmer_id", escrowAccount.getFarmerId().toString());
        event.put("investor_id", escrowAccount.getInvestorId().toString());
        event.put("amount_etb", escrowAccount.getTotalAmountEtb());
        event.put("lock_expires_at", escrowAccount.getLockExpiresAt().toString());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing escrow.locked for investment: {}",
                escrowAccount.getInvestmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ESCROW_EXCHANGE,
                RabbitMQConfig.ESCROW_LOCKED_KEY,
                event);
    }

    @Override
    public void publishEscrowPartiallyReleased(EscrowAccount escrowAccount,
                                               EscrowRelease release) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "escrow.partially.released");
        event.put("escrow_id", escrowAccount.getId().toString());
        event.put("investment_id", escrowAccount.getInvestmentId().toString());
        event.put("farmer_id", escrowAccount.getFarmerId().toString());
        event.put("voucher_id", release.getVoucherId() != null
                ? release.getVoucherId().toString() : null);
        event.put("released_amount_etb", release.getAmountEtb());
        event.put("remaining_amount_etb", escrowAccount.getRemainingLockedAmountEtb());
        event.put("release_reason", release.getReleaseReason());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing escrow.partially.released for investment: {}",
                escrowAccount.getInvestmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ESCROW_EXCHANGE,
                RabbitMQConfig.ESCROW_PARTIAL_RELEASED_KEY,
                event);
    }

    @Override
    public void publishEscrowFullyReleased(EscrowAccount escrowAccount) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "escrow.fully.released");
        event.put("escrow_id", escrowAccount.getId().toString());
        event.put("investment_id", escrowAccount.getInvestmentId().toString());
        event.put("farmer_id", escrowAccount.getFarmerId().toString());
        event.put("investor_id", escrowAccount.getInvestorId().toString());
        event.put("total_amount_etb", escrowAccount.getTotalAmountEtb());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing escrow.fully.released for investment: {}",
                escrowAccount.getInvestmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ESCROW_EXCHANGE,
                RabbitMQConfig.ESCROW_FULLY_RELEASED_KEY,
                event);
    }

    @Override
    public void publishEscrowCancelled(EscrowAccount escrowAccount) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "escrow.cancelled");
        event.put("escrow_id", escrowAccount.getId().toString());
        event.put("investment_id", escrowAccount.getInvestmentId().toString());
        event.put("investor_id", escrowAccount.getInvestorId().toString());
        event.put("refund_amount_etb",
                escrowAccount.getTotalAmountEtb().subtract(escrowAccount.getReleasedAmountEtb()));
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing escrow.cancelled for investment: {}",
                escrowAccount.getInvestmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ESCROW_EXCHANGE,
                RabbitMQConfig.ESCROW_CANCELLED_KEY,
                event);
    }
}