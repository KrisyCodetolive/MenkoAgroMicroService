package com.menkoagro.api.modules.production.infrastructure.repository;

import com.menkoagro.api.modules.production.domain.entity.CoutProduction;
import com.menkoagro.api.modules.production.domain.repository.CoutProductionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CoutProductionJpaRepository extends JpaRepository<CoutProduction, UUID>, CoutProductionRepository {

    List<CoutProduction> findByProductionId(UUID productionId);
}
