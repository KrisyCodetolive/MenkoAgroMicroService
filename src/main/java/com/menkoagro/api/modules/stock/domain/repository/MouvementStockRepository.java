package com.menkoagro.api.modules.stock.domain.repository;

import com.menkoagro.api.modules.stock.domain.entity.MouvementStock;
import com.menkoagro.api.modules.stock.domain.entity.TypeMouvement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MouvementStockRepository {

    List<MouvementStock> findByStockIdOrderByDateDesc(UUID stockId);

    Optional<MouvementStock> findFirstByStockIdAndTypeOrderByDateDesc(UUID stockId, TypeMouvement type);

    MouvementStock save(MouvementStock mouvement);
}
