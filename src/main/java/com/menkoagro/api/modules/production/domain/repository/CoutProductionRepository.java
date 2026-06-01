package com.menkoagro.api.modules.production.domain.repository;

import com.menkoagro.api.modules.production.domain.entity.CoutProduction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoutProductionRepository {

    List<CoutProduction> findByProductionId(UUID productionId);

    List<CoutProduction> findByEtapeId(UUID etapeId);

    Optional<CoutProduction> findById(UUID id);

    CoutProduction save(CoutProduction cout);

    void deleteById(UUID id);
}
