package com.menkoagro.api.modules.production.domain.repository;

import com.menkoagro.api.modules.production.domain.entity.Production;
import com.menkoagro.api.modules.production.domain.entity.StatutProduction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductionRepository {

    Optional<Production> findById(UUID id);

    List<Production> findAll();

    List<Production> findByProduitId(UUID produitId);

    List<Production> findByStatut(StatutProduction statut);

    List<Production> findByStatutAndDateFinBetween(StatutProduction statut, LocalDate debut, LocalDate fin);

    Production save(Production production);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
