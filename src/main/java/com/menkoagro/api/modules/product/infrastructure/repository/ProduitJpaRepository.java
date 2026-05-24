package com.menkoagro.api.modules.product.infrastructure.repository;

import com.menkoagro.api.modules.product.domain.entity.Produit;
import com.menkoagro.api.modules.product.domain.repository.ProduitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProduitJpaRepository extends JpaRepository<Produit, UUID>, ProduitRepository {

    List<Produit> findByCategorieId(UUID categorieId);

    boolean existsByNomAndCategorieId(String nom, UUID categorieId);
}
