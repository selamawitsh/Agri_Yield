package com.agriyield.voucherservice.infrastructure.adapter.outgoing.messaging;

import com.agriyield.voucherservice.application.port.outgoing.EventPublisherPort;
import com.agriyield.voucherservice.domain.model.Voucher;
import com.agriyield.voucherservice.domain.model.VoucherRedemption;
import com.agriyield.voucherservice.infrastructure.config.RabbitMQConfig;
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
public class VoucherEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishVouchersGenerated(List<Voucher> vouchers) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "voucher.generated");
        event.put("count", vouchers.size());
        event.put("investment_id", vouchers.get(0).getInvestmentId().toString());
        event.put("farm_id", vouchers.get(0).getFarmId().toString());
        event.put("farmer_id", vouchers.get(0).getFarmerId().toString());
        event.put("vouchers", vouchers.stream().map(v -> {
            Map<String, Object> vMap = new HashMap<>();
            vMap.put("voucher_id", v.getId().toString());
            vMap.put("voucher_code", v.getVoucherCode());
            vMap.put("product_name", v.getProductName());
            vMap.put("amount_etb", v.getAmountEtb());
            vMap.put("expires_at", v.getExpiresAt().toString());
            return vMap;
        }).collect(Collectors.toList()));
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing voucher.generated for investment: {}",
            vouchers.get(0).getInvestmentId());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.VOUCHER_EXCHANGE,
            RabbitMQConfig.VOUCHER_GENERATED_KEY, event);
    }

    @Override
    public void publishVoucherRedeemed(Voucher voucher, VoucherRedemption redemption) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "voucher.redeemed");
        event.put("voucher_id", voucher.getId().toString());
        event.put("voucher_code", voucher.getVoucherCode());
        event.put("investment_id", voucher.getInvestmentId().toString());
        event.put("farm_id", voucher.getFarmId().toString());
        event.put("farmer_id", voucher.getFarmerId().toString());
        event.put("merchant_id", voucher.getMerchantId().toString());
        event.put("amount_etb", voucher.getAmountEtb());
        event.put("escrow_released", redemption.getEscrowReleased());
        event.put("redeemed_at", redemption.getRedeemedAt().toString());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing voucher.redeemed: {}", voucher.getVoucherCode());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.VOUCHER_EXCHANGE,
            RabbitMQConfig.VOUCHER_REDEEMED_KEY, event);
    }

    @Override
    public void publishVoucherExpired(Voucher voucher) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "voucher.expired");
        event.put("voucher_id", voucher.getId().toString());
        event.put("voucher_code", voucher.getVoucherCode());
        event.put("investment_id", voucher.getInvestmentId().toString());
        event.put("farm_id", voucher.getFarmId().toString());
        event.put("amount_etb", voucher.getAmountEtb());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing voucher.expired: {}", voucher.getVoucherCode());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.VOUCHER_EXCHANGE,
            RabbitMQConfig.VOUCHER_EXPIRED_KEY, event);
    }

    @Override
    public void publishVoucherCancelled(Voucher voucher) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "voucher.cancelled");
        event.put("voucher_id", voucher.getId().toString());
        event.put("voucher_code", voucher.getVoucherCode());
        event.put("investment_id", voucher.getInvestmentId().toString());
        event.put("farm_id", voucher.getFarmId().toString());
        event.put("amount_etb", voucher.getAmountEtb());
        event.put("timestamp", LocalDateTime.now().toString());

        log.info("Publishing voucher.cancelled: {}", voucher.getVoucherCode());
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.VOUCHER_EXCHANGE,
            RabbitMQConfig.VOUCHER_CANCELLED_KEY, event);
    }
}
