package com.menkoagro.api.shared.security;

import com.menkoagro.api.modules.auth.domain.entity.Utilisateur;
import com.menkoagro.api.modules.auth.domain.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));

        if (!utilisateur.isActif()) {
            throw new UsernameNotFoundException("Compte désactivé : " + email);
        }

        List<SimpleGrantedAuthority> permissionAuthorities = utilisateur.getRole().getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority(p.getCode()))
                .collect(Collectors.toList());

        // Ajoute le rôle en tant qu'autorité Spring Security standard
        String roleAuthority = "ROLE_" + utilisateur.getRole().getNom()
                .toUpperCase()
                .replace(" ", "_")
                .replace(".", "");

        List<SimpleGrantedAuthority> allAuthorities = Stream.concat(
                permissionAuthorities.stream(),
                Stream.of(new SimpleGrantedAuthority(roleAuthority))
        ).collect(Collectors.toList());

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasseHash())
                .authorities(allAuthorities)
                .build();
    }
}
