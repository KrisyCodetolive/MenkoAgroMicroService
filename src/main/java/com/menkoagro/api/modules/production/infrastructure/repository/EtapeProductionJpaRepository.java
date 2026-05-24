package com.menkoagro.api.modules.production.infrastructure.repository;

import com.menkoagro.api.modules.production.domain.entity.EtapeProduction;
import com.menkoagro.api.modules.production.domain.repository.EtapeProductionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EtapeProductionJpaRepository extends JpaRepository<EtapeProduction, UUID>, EtapeProductionRepository {

    @Query("SELECT e FROM EtapeProduction e WHERE e.production.id = :productionId ORDER BY e.dateRealisation ASC")
    List<EtapeProduction> findByProductionId(@Param("productionId") UUID productionId);
}
