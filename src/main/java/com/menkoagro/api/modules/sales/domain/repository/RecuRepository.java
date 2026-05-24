package com.menkoagro.api.modules.sales.domain.repository;

import com.menkoagro.api.modules.sales.domain.entity.Recu;

import java.util.Optional;
import java.util.UUID;

public interface RecuRepository {

    Optional<Recu> findByVenteId(UUID venteId);

    boolean existsByNumeroRecu(String numeroRecu);

    Recu save(Recu recu);
}
