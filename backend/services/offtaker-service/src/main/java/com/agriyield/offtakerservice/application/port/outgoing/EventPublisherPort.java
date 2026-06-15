package com.agriyield.offtakerservice.application.port.outgoing;

import java.math.BigDecimal;
import java.util.UUID;

public interface EventPublisherPort {
    void publishBidPlaced(UUID bidId, UUID farmId, UUID offtakerId,
                          BigDecimal quantityQuintals, BigDecimal pricePerQuintalEtb,
                          BigDecimal totalValueEtb, String expiresAt);

    void publishBidAccepted(UUID bidId, UUID farmId, UUID farmerId,
                            UUID offtakerId, UUID agreementId);

    /**
     * FIX: This method was completely missing from the port and publisher.
     * SRS §5.2 explicitly lists logistics.dispatched as a required event
     * on offtaker.exchange. DispatchServiceImpl.scheduleDispatch() never
     * published it — notification-service and farm-service never received it.
     */
    void publishLogisticsDispatched(UUID dispatchId, UUID agreementId,
                                    UUID offtakerId, String driverFaydaId,
                                    int truckCount, String scheduledPickupDate);

    void publishHarvestConfirmed(UUID farmId, UUID agreementId,
                                 BigDecimal actualQuantityQuintals,
                                 String qualityGrade, BigDecimal totalPaymentEtb);

    void publishOfftakerDefaulted(UUID bidId, UUID farmId,
                                  UUID offtakerId, BigDecimal forfeitAmountEtb);
}