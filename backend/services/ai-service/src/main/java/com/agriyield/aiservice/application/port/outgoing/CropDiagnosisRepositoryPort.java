package com.agriyield.aiservice.application.port.outgoing;

import com.agriyield.aiservice.domain.model.CropDiagnosis;
import java.util.List;

public interface CropDiagnosisRepositoryPort {

    CropDiagnosis save(CropDiagnosis diagnosis);

    List<CropDiagnosis> findByFarmId(String farmId);

    List<CropDiagnosis> findByFarmerIdAndEscalateToAgronomist(
            String farmerId,
            boolean escalate
    );
}