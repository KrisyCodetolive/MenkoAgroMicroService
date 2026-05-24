package com.menkoagro.api.modules.sales.infrastructure.repository;

import com.menkoagro.api.modules.sales.domain.entity.Recu;
import com.menkoagro.api.modules.sales.domain.repository.RecuRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecuJpaRepository extends JpaRepository<Recu, UUID>, RecuRepository {

    Optional<Recu> findByVenteId(UUID venteId);

    boolean existsByNumeroRecu(String numeroRecu);
}
