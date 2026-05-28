package com.agriyield.fraudservice.application.port.outgoing;

import com.agriyield.fraudservice.domain.model.FraudAlert;

public interface FraudEventPublisherPort {

    void publishFraudAlertHigh(FraudAlert alert);

    void publishFraudAlertCritical(FraudAlert alert);
}
