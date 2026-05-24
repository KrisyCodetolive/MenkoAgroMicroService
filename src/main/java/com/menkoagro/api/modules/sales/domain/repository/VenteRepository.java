package com.menkoagro.api.modules.sales.domain.repository;

import com.menkoagro.api.modules.sales.domain.entity.Vente;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VenteRepository {

    Optional<Vente> findById(UUID id);

    List<Vente> findAll();

    List<Vente> findByClientIdOrderByDateVenteDesc(UUID clientId);

    List<Vente> findByDateVenteBetween(LocalDateTime debut, LocalDateTime fin);

    Vente save(Vente vente);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
