package com.menkoagro.api.modules.auth.domain.repository;

import com.menkoagro.api.modules.auth.domain.entity.Role;

import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByNom(String nom);
}
