package com.menkoagro.api.modules.product.infrastructure.repository;

import com.menkoagro.api.modules.product.domain.entity.TypeCategorie;
import com.menkoagro.api.modules.product.domain.repository.TypeCategorieRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TypeCategorieJpaRepository extends JpaRepository<TypeCategorie, UUID>, TypeCategorieRepository {

    boolean existsByNom(String nom);

    boolean existsByNomAndIdNot(String nom, UUID id);
}
