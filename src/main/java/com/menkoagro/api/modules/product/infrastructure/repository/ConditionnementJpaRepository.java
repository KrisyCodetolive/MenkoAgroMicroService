package com.menkoagro.api.modules.product.infrastructure.repository;

import com.menkoagro.api.modules.product.domain.entity.Conditionnement;
import com.menkoagro.api.modules.product.domain.repository.ConditionnementRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConditionnementJpaRepository extends JpaRepository<Conditionnement, UUID>, ConditionnementRepository {

    List<Conditionnement> findByProduitId(UUID produitId);
}
