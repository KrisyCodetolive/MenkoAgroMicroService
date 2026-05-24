package com.menkoagro.api.modules.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Réponse de connexion avec token JWT")
public class LoginResponse {

    @Schema(description = "Token JWT Bearer à inclure dans l'en-tête Authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Type du token", example = "Bearer")
    private String type;

    @Schema(description = "Informations de l'utilisateur connecté")
    private UtilisateurDto utilisateur;
}
