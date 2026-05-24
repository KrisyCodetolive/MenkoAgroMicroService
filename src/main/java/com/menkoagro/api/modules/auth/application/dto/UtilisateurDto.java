package com.menkoagro.api.modules.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Profil d'un utilisateur avec ses permissions")
public class UtilisateurDto {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Nom complet de l'utilisateur", example = "Administrateur")
    private String nom;

    @Schema(description = "Adresse email", example = "admin@menkoagro.com")
    private String email;

    @Schema(description = "Rôle de l'utilisateur", example = "Administrateur")
    private String role;

    @Schema(description = "Liste des codes de permissions accordés", example = "[\"VENTE_VOIR\", \"STOCK_VOIR\", \"RAPPORT_VOIR\"]")
    private List<String> permissions;
}
