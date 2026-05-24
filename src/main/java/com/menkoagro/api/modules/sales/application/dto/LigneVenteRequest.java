package com.menkoagro.api.modules.sales.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Ligne d'une vente : produit, conditionnement et quantité")
public class LigneVenteRequest {

    @NotNull(message = "Le produit est obligatoire")
    @Schema(description = "Identifiant du produit à vendre", example = "660e8400-e29b-41d4-a716-446655440001",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID produitId;

    @NotNull(message = "Le conditionnement est obligatoire")
    @Schema(description = "Identifiant du conditionnement (détermine l'unité et le prix)", example = "aa0e8400-e29b-41d4-a716-446655440005",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID conditionnementId;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.001", message = "La quantité doit être supérieure à zéro")
    @Schema(description = "Nombre de conditionnements vendus (ex: 5 sacs)", example = "5.000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal quantite;
}
