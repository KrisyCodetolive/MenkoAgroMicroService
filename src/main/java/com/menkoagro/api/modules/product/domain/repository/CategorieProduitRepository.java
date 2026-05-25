package com.menkoagro.api.modules.product.domain.repository;

import com.menkoagro.api.modules.product.domain.entity.CategorieProduit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategorieProduitRepository {

    Optional<CategorieProduit> findById(UUID id);

    List<CategorieProduit> findAll();

    List<CategorieProduit> findByTypeCategorieId(UUID typeCategorieId);

    CategorieProduit save(CategorieProduit categorieProduit);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    boolean existsByNomAndTypeCategorieId(String nom, UUID typeCategorieId);

    boolean existsByNomAndTypeCategorieIdAndIdNot(String nom, UUID typeCategorieId, UUID id);

    boolean existsByTypeCategorieId(UUID typeCategorieId);
}
