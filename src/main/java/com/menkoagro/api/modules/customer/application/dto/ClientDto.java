package com.menkoagro.api.modules.customer.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Fiche client")
public class ClientDto {

    @Schema(description = "Identifiant du client", example = "bb0e8400-e29b-41d4-a716-446655440006")
    private UUID id;

    @Schema(description = "Nom complet du client", example = "Moussa Coulibaly")
    private String nom;

    @Schema(description = "Numéro de téléphone", example = "+22507070707")
    private String telephone;

    @Schema(description = "Adresse email", example = "moussa.coulibaly@example.com")
    private String email;

    @Schema(description = "Adresse physique", example = "Quartier Commerce, Abidjan")
    private String adresse;

    @Schema(description = "true si le client a au moins un téléphone ou un email (peut recevoir des relances)", example = "true")
    private boolean peutEtreRelance;

    @Schema(description = "Date de création du compte client", example = "2024-01-15T09:00:00")
    private LocalDateTime createdAt;
}
