package com.menkoagro.api.modules.auth.application.service;

import com.menkoagro.api.modules.auth.application.dto.LoginRequest;
import com.menkoagro.api.modules.auth.application.dto.LoginResponse;
import com.menkoagro.api.modules.auth.application.dto.UtilisateurDto;
import com.menkoagro.api.modules.auth.domain.entity.Utilisateur;
import com.menkoagro.api.modules.auth.domain.repository.UtilisateurRepository;
import com.menkoagro.api.shared.exception.BusinessException;
import com.menkoagro.api.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED));

        if (!utilisateur.isActif()) {
            throw new BusinessException("Ce compte est désactivé", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasseHash())) {
            throw new BusinessException("Email ou mot de passe incorrect", HttpStatus.UNAUTHORIZED);
        }

        utilisateur.setDernierAcces(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .utilisateur(toDto(utilisateur))
                .build();
    }

    @Transactional(readOnly = true)
    public UtilisateurDto getProfile(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
        return toDto(utilisateur);
    }

    private UtilisateurDto toDto(Utilisateur utilisateur) {
        List<String> permissions = utilisateur.getRole().getPermissions().stream()
                .map(p -> p.getCode())
                .collect(Collectors.toList());

        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .role(utilisateur.getRole().getNom())
                .permissions(permissions)
                .build();
    }
}
