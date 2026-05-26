package com.agriyield.voucherservice.infrastructure.config;

import com.agriyield.voucherservice.application.port.incoming.VoucherServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoucherExpiryScheduler {

    private final VoucherServicePort voucherService;

    /** Run every 6 hours to expire overdue vouchers */
    @Scheduled(fixedRate = 21600000)
    public void expireOverdueVouchers() {
        log.info("Scheduler: checking for expired vouchers...");
        voucherService.expireOverdueVouchers();
    }
}
