package com.menkoagro.api.modules.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Données pour démarrer une production agricole")
public class ProductionAgricoleRequest {

    @NotNull(message = "Le produit est obligatoire")
    @Schema(description = "Produit qui sera cultivé", example = "660e8400-e29b-41d4-a716-446655440001",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID produitId;

    @NotNull(message = "La date de début est obligatoire")
    @Schema(description = "Date de démarrage de la campagne", example = "2024-01-15",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;

    @Schema(description = "Zone géographique de la culture", example = "Ferme Nord — Parcelle B")
    private String zone;

    @Schema(description = "Superficie de la parcelle en hectares", example = "2.50")
    private BigDecimal superficieParcelle;
}
