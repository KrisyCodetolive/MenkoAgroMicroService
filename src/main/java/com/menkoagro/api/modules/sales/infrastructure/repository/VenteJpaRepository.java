package com.menkoagro.api.modules.sales.infrastructure.repository;

import com.menkoagro.api.modules.sales.domain.entity.Vente;
import com.menkoagro.api.modules.sales.domain.repository.VenteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface VenteJpaRepository extends JpaRepository<Vente, UUID>, VenteRepository {

    List<Vente> findByClientIdOrderByDateVenteDesc(UUID clientId);

    List<Vente> findByDateVenteBetween(LocalDateTime debut, LocalDateTime fin);
}
