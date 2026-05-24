package com.menkoagro.api.modules.product.domain.repository;

import com.menkoagro.api.modules.product.domain.entity.Conditionnement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConditionnementRepository {

    Optional<Conditionnement> findById(UUID id);

    List<Conditionnement> findByProduitId(UUID produitId);

    Conditionnement save(Conditionnement conditionnement);

    void deleteById(UUID id);
}
