package com.menkoagro.api.modules.stock.infrastructure.repository;

import com.menkoagro.api.modules.stock.domain.entity.Stock;
import com.menkoagro.api.modules.stock.domain.repository.StockRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockJpaRepository extends JpaRepository<Stock, UUID>, StockRepository {

    Optional<Stock> findByProduitId(UUID produitId);

    boolean existsByProduitId(UUID produitId);

    @Query("SELECT s FROM Stock s WHERE s.quantite <= s.seuilAlerte")
    List<Stock> findStocksEnAlerte();

    @Query("SELECT s FROM Stock s WHERE s.produit.estPerissable = true AND s.quantite > 0")
    List<Stock> findStocksPerissables();
}
