package com.menkoagro.api.shared.config;

import com.menkoagro.api.modules.auth.domain.entity.Utilisateur;
import com.menkoagro.api.modules.auth.domain.repository.RoleRepository;
import com.menkoagro.api.modules.auth.domain.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@menkoagro.com";
    private static final String ADMIN_PASSWORD = "MenkoAdmin2024!";

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (utilisateurRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        roleRepository.findByNom("Administrateur").ifPresentOrElse(
                adminRole -> {
                    Utilisateur admin = Utilisateur.builder()
                            .nom("Administrateur")
                            .email(ADMIN_EMAIL)
                            .motDePasseHash(passwordEncoder.encode(ADMIN_PASSWORD))
                            .role(adminRole)
                            .actif(true)
                            .build();
                    utilisateurRepository.save(admin);
                    log.info("Utilisateur admin créé : {}", ADMIN_EMAIL);
                },
                () -> log.warn("Rôle 'Administrateur' introuvable — admin non créé")
        );
    }
}
