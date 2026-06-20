package com.agriyield.aiservice.infrastructure.adapter.outgoing.persistence;

import com.agriyield.aiservice.application.port.outgoing.AdvisorySessionRepositoryPort;
import com.agriyield.aiservice.domain.model.AdvisorySession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
interface SpringAdvisorySessionRepo extends MongoRepository<AdvisorySession, String> {

    List<AdvisorySession> findByFarmId(String farmId);

    List<AdvisorySession> findByFarmerId(String farmerId);
}

@Component
@RequiredArgsConstructor
class AdvisorySessionRepositoryAdapter implements AdvisorySessionRepositoryPort {

    private final SpringAdvisorySessionRepo repo;

    @Override
    public AdvisorySession save(AdvisorySession session) {
        return repo.save(session);
    }

    @Override
    public List<AdvisorySession> findByFarmId(String farmId) {
        return repo.findByFarmId(farmId);
    }

    @Override
    public List<AdvisorySession> findByFarmerId(String farmerId) {
        return repo.findByFarmerId(farmerId);
    }
}
