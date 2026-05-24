package com.menkoagro.api.modules.sales.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Reçu PDF généré automatiquement lors d'une vente")
public class RecuDto {

    @Schema(description = "Identifiant du reçu", example = "330e8400-e29b-41d4-a716-446655440013")
    private UUID id;

    @Schema(description = "Numéro de reçu formaté (REC-YYYYMMDD-XXXXXXXX)", example = "REC-20240315-A1B2C3D4")
    private String numeroRecu;

    @Schema(description = "Chemin du fichier PDF sur le serveur", example = "./receipts/REC-20240315-A1B2C3D4.pdf")
    private String cheminFichier;

    @Schema(description = "Date et heure de génération du reçu", example = "2024-03-15T14:30:05")
    private LocalDateTime genereA;
}
