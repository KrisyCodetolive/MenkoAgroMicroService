package com.menkoagro.api.modules.auth.infrastructure.repository;

import com.menkoagro.api.modules.auth.domain.entity.Utilisateur;
import com.menkoagro.api.modules.auth.domain.repository.UtilisateurRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UtilisateurJpaRepository extends JpaRepository<Utilisateur, UUID>, UtilisateurRepository {
}
