package com.menkoagro.api.modules.product.infrastructure.repository;

import com.menkoagro.api.modules.product.domain.entity.CategorieProduit;
import com.menkoagro.api.modules.product.domain.repository.CategorieProduitRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategorieProduitJpaRepository extends JpaRepository<CategorieProduit, UUID>, CategorieProduitRepository {

    List<CategorieProduit> findByTypeCategorieId(UUID typeCategorieId);
}
