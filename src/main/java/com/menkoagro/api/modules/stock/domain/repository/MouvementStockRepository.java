package com.menkoagro.api.modules.stock.domain.repository;

import com.menkoagro.api.modules.stock.domain.entity.MouvementStock;

import java.util.List;
import java.util.UUID;

public interface MouvementStockRepository {

    List<MouvementStock> findByStockIdOrderByDateDesc(UUID stockId);

    MouvementStock save(MouvementStock mouvement);
}
