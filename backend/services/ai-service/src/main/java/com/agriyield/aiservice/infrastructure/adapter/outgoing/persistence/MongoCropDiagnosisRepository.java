package com.agriyield.aiservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.aiservice.application.port.outgoing.CropDiagnosisRepositoryPort;
import com.agriyield.aiservice.domain.model.CropDiagnosis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
interface SpringCropDiagnosisRepo extends MongoRepository<CropDiagnosis, String> {

    List<CropDiagnosis> findByFarmId(String farmId);

    List<CropDiagnosis> findByFarmerIdAndEscalateToAgronomist(
            String farmerId,
            boolean escalateToAgronomist
    );
}

@Component
@RequiredArgsConstructor
class CropDiagnosisRepositoryAdapter implements CropDiagnosisRepositoryPort {

    private final SpringCropDiagnosisRepo repo;

    @Override
    public CropDiagnosis save(CropDiagnosis diagnosis) {
        return repo.save(diagnosis);
    }

    @Override
    public List<CropDiagnosis> findByFarmId(String farmId) {
        return repo.findByFarmId(farmId);
    }

    @Override
    public List<CropDiagnosis> findByFarmerIdAndEscalateToAgronomist(
            String farmerId,
            boolean escalate) {

        return repo.findByFarmerIdAndEscalateToAgronomist(
                farmerId,
                escalate
        );
    }
}