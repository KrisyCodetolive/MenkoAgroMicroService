package com.menkoagro.api.modules.customer.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Données pour créer ou modifier un client")
public class ClientRequest {

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Schema(description = "Nom complet du client", example = "Moussa Coulibaly", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    @Schema(description = "Numéro de téléphone (requis pour les notifications SMS)", example = "+22507070707")
    private String telephone;

    @Email(message = "Format d'email invalide")
    @Size(max = 150)
    @Schema(description = "Adresse email (requise pour les notifications EMAIL)", example = "moussa.coulibaly@example.com")
    private String email;

    @Schema(description = "Adresse physique", example = "Quartier Commerce, Abidjan")
    private String adresse;
}
