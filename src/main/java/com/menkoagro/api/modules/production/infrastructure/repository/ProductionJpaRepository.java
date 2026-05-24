package com.menkoagro.api.modules.production.infrastructure.repository;

import com.menkoagro.api.modules.production.domain.entity.Production;
import com.menkoagro.api.modules.production.domain.entity.StatutProduction;
import com.menkoagro.api.modules.production.domain.repository.ProductionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductionJpaRepository extends JpaRepository<Production, UUID>, ProductionRepository {

    List<Production> findByProduitId(UUID produitId);

    List<Production> findByStatut(StatutProduction statut);

    List<Production> findByStatutAndDateFinBetween(StatutProduction statut, LocalDate debut, LocalDate fin);
}
