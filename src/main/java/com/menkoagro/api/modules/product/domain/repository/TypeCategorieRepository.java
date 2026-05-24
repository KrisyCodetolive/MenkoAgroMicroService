package com.menkoagro.api.modules.product.domain.repository;

import com.menkoagro.api.modules.product.domain.entity.TypeCategorie;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TypeCategorieRepository {

    Optional<TypeCategorie> findById(UUID id);

    List<TypeCategorie> findAll();
}
