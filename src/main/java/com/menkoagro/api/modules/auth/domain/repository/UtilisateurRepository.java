package com.menkoagro.api.modules.auth.domain.repository;

import com.menkoagro.api.modules.auth.domain.entity.Utilisateur;

import java.util.Optional;
import java.util.UUID;

public interface UtilisateurRepository {

    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findById(UUID id);

    Utilisateur save(Utilisateur utilisateur);

    boolean existsByEmail(String email);
}
