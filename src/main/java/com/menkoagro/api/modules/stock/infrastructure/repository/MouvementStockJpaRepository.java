package com.menkoagro.api.modules.stock.infrastructure.repository;

import com.menkoagro.api.modules.stock.domain.entity.MouvementStock;
import com.menkoagro.api.modules.stock.domain.repository.MouvementStockRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MouvementStockJpaRepository extends JpaRepository<MouvementStock, UUID>, MouvementStockRepository {

    List<MouvementStock> findByStockIdOrderByDateDesc(UUID stockId);
}
