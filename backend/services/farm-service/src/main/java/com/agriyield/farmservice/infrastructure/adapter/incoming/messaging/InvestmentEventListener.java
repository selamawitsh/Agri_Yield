package com.agriyield.farmservice.infrastructure.adapter.incoming.messaging;

import com.agriyield.farmservice.application.port.outgoing.InputNeedRepositoryPort;
import com.agriyield.farmservice.domain.model.InputNeed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvestmentEventListener {

    private final InputNeedRepositoryPort inputNeedRepository;

    /**
     * Listen for investment.escrow.locked events published by investment-service.
     * When an investor's funds are locked in escrow we increment the related InputNeed's funded amount
     * so the farmer UI and farm-service reflect progress in near real-time.
     *
     * Expected event payload fields (published by investment-service):
     * - input_need_id (String UUID)
     * - amount_etb (Number)
     */
    @RabbitListener(queues = "farm.investment-events.queue")
    public void onInvestmentEvent(Map<String, Object> event) {
        try {
            log.info("Received investment event: {}", event);
            if (event == null || event.isEmpty()) return;

            String eventType = (String) event.getOrDefault("event_type", "");
            Object inputNeedObj = event.get("input_need_id");
            Object amountObj = event.get("amount_etb");

            if ("investment.escrow.locked".equals(eventType)) {
                if (inputNeedObj == null || amountObj == null) {
                    log.warn("investment.escrow.locked missing input_need_id or amount_etb: {}", event);
                    return;
                }
                UUID inputNeedId = UUID.fromString(inputNeedObj.toString());
                BigDecimal amount = new BigDecimal(amountObj.toString());

                Optional<InputNeed> maybe = inputNeedRepository.findById(inputNeedId);
                if (maybe.isPresent()) {
                    InputNeed inputNeed = maybe.get();
                    log.info("Applying escrow-locked +{} ETB to InputNeed {}", amount, inputNeedId);
                    if (inputNeed.getFundedAmountEtb() == null) inputNeed.setFundedAmountEtb(BigDecimal.ZERO);
                    inputNeed.updateFundedAmount(amount);
                    inputNeedRepository.save(inputNeed);
                    log.info("InputNeed {} updated: funded={} / total={} (status={})",
                        inputNeed.getId(), inputNeed.getFundedAmountEtb(), inputNeed.getTotalAmountEtb(), inputNeed.getStatus());
                } else {
                    log.warn("InputNeed not found for id={} — event ignored", inputNeedId);
                }

            } else if ("investment.cancelled".equals(eventType)) {
                if (inputNeedObj == null || amountObj == null) {
                    log.warn("investment.cancelled missing input_need_id or amount_etb: {}", event);
                    return;
                }
                UUID inputNeedId = UUID.fromString(inputNeedObj.toString());
                BigDecimal amount = new BigDecimal(amountObj.toString());
                Optional<InputNeed> maybe = inputNeedRepository.findById(inputNeedId);
                if (maybe.isPresent()) {
                    InputNeed inputNeed = maybe.get();
                    log.info("Applying investment cancelled -{} ETB to InputNeed {}", amount, inputNeedId);
                    if (inputNeed.getFundedAmountEtb() == null) inputNeed.setFundedAmountEtb(BigDecimal.ZERO);
                    BigDecimal newFunded = inputNeed.getFundedAmountEtb().subtract(amount);
                    if (newFunded.compareTo(BigDecimal.ZERO) <= 0) {
                        inputNeed.setFundedAmountEtb(BigDecimal.ZERO);
                        inputNeed.setStatus(com.agriyield.farmservice.domain.enums.InputNeedStatus.OPEN);
                    } else {
                        inputNeed.setFundedAmountEtb(newFunded);
                        inputNeed.setStatus(com.agriyield.farmservice.domain.enums.InputNeedStatus.PARTIALLY_FUNDED);
                    }
                    inputNeedRepository.save(inputNeed);
                    log.info("InputNeed {} updated after cancel: funded={} (status={})",
                        inputNeed.getId(), inputNeed.getFundedAmountEtb(), inputNeed.getStatus());
                }

            } else if ("listing.fully.funded".equals(eventType)) {
                if (inputNeedObj == null) {
                    log.warn("listing.fully.funded missing input_need_id: {}", event);
                    return;
                }
                UUID inputNeedId = UUID.fromString(inputNeedObj.toString());
                Optional<InputNeed> maybe = inputNeedRepository.findById(inputNeedId);
                if (maybe.isPresent()) {
                    InputNeed inputNeed = maybe.get();
                    log.info("Marking InputNeed {} as FULLY_FUNDED", inputNeedId);
                    inputNeed.setFundedAmountEtb(inputNeed.getTotalAmountEtb());
                    inputNeed.setStatus(com.agriyield.farmservice.domain.enums.InputNeedStatus.FULLY_FUNDED);
                    inputNeedRepository.save(inputNeed);
                }

            } else if ("listing.funding.failed".equals(eventType)) {
                if (inputNeedObj == null) {
                    log.warn("listing.funding.failed missing input_need_id: {}", event);
                    return;
                }
                UUID inputNeedId = UUID.fromString(inputNeedObj.toString());
                Optional<InputNeed> maybe = inputNeedRepository.findById(inputNeedId);
                if (maybe.isPresent()) {
                    InputNeed inputNeed = maybe.get();
                    log.info("Listing funding failed for InputNeed {} — resetting funded amount and reopening", inputNeedId);
                    inputNeed.setFundedAmountEtb(BigDecimal.ZERO);
                    inputNeed.setStatus(com.agriyield.farmservice.domain.enums.InputNeedStatus.OPEN);
                    inputNeedRepository.save(inputNeed);
                }

            } else {
                log.debug("Unhandled investment event type: {}", eventType);
            }

        } catch (Exception e) {
            log.error("Failed to process investment event: {}", e.getMessage(), e);
        }
    }
}
