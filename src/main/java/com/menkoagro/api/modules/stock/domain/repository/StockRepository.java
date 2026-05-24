package com.menkoagro.api.modules.stock.domain.repository;

import com.menkoagro.api.modules.stock.domain.entity.Stock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockRepository {

    Optional<Stock> findById(UUID id);

    Optional<Stock> findByProduitId(UUID produitId);

    List<Stock> findAll();

    List<Stock> findStocksEnAlerte();

    Stock save(Stock stock);

    boolean existsByProduitId(UUID produitId);
}
