package com.menkoagro.api.modules.product.domain.repository;

import com.menkoagro.api.modules.product.domain.entity.Produit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProduitRepository {

    Optional<Produit> findById(UUID id);

    List<Produit> findAll();

    List<Produit> findByCategorieId(UUID categorieId);

    boolean existsByNomAndCategorieId(String nom, UUID categorieId);

    boolean existsById(UUID id);

    Produit save(Produit produit);

    void deleteById(UUID id);
}
