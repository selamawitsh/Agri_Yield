package com.agriyield.offtakerservice.application.port.incoming;

import com.agriyield.offtakerservice.domain.model.TruckDispatch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DispatchServicePort {
    TruckDispatch scheduleDispatch(UUID offtakerId, UUID agreementId,
                                   String driverFaydaId, int truckCount,
                                   LocalDate scheduledPickupDate);
    TruckDispatch confirmArrival(UUID dispatchId, UUID farmerId);
    TruckDispatch confirmLoading(UUID dispatchId, UUID farmerId);
    TruckDispatch confirmDelivery(UUID agreementId, UUID offtakerId,
                                  BigDecimal actualQuantityQuintals, String qualityGrade);
    List<TruckDispatch> getDispatchesForAgreement(UUID agreementId);
}
