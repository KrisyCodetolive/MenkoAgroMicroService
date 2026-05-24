package com.menkoagro.api.modules.auth.presentation.controller;

import com.menkoagro.api.modules.auth.application.dto.LoginRequest;
import com.menkoagro.api.modules.auth.application.dto.LoginResponse;
import com.menkoagro.api.modules.auth.application.dto.UtilisateurDto;
import com.menkoagro.api.modules.auth.application.service.AuthService;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Connexion et profil utilisateur")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(summary = "Connexion", description = "Retourne un token JWT Bearer valide 24h à inclure dans chaque requête")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Connexion réussie — token retourné",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email ou mot de passe manquant / invalide"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Connexion réussie", response));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Profil courant", description = "Retourne les informations de l'utilisateur connecté (extrait du token JWT)")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profil retourné",
                    content = @Content(schema = @Schema(implementation = UtilisateurDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token absent ou expiré"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    public ResponseEntity<ApiResponse<UtilisateurDto>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        UtilisateurDto dto = authService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(dto));
    }
}
