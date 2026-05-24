package com.menkoagro.api.modules.production.domain.repository;

import com.menkoagro.api.modules.production.domain.entity.EtapeProduction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EtapeProductionRepository {

    List<EtapeProduction> findByProductionId(UUID productionId);

    Optional<EtapeProduction> findById(UUID id);

    EtapeProduction save(EtapeProduction etape);

    void deleteById(UUID id);
}
