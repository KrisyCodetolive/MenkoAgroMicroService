package com.menkoagro.api.modules.sales.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Ligne de vente (un produit dans un conditionnement donné)")
public class LigneVenteDto {

    @Schema(description = "Identifiant de la ligne", example = "220e8400-e29b-41d4-a716-446655440012")
    private UUID id;

    @Schema(description = "Identifiant du produit vendu", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID produitId;

    @Schema(description = "Nom du produit", example = "Poulet de chair")
    private String nomProduit;

    @Schema(description = "Identifiant du conditionnement utilisé", example = "aa0e8400-e29b-41d4-a716-446655440005")
    private UUID conditionnementId;

    @Schema(description = "Libellé du conditionnement", example = "Plateau 30 œufs")
    private String libelleConditionnement;

    @Schema(description = "Quantité de conditionnements vendus", example = "5.000")
    private BigDecimal quantite;

    @Schema(description = "Prix unitaire du conditionnement en FCFA", example = "15000.00")
    private BigDecimal prixUnitaire;

    @Schema(description = "Sous-total = quantite × prixUnitaire en FCFA", example = "75000.00")
    private BigDecimal sousTotal;

    @Schema(description = "Quantité déduite du stock = quantite × conditionnement.quantiteBase", example = "150.000")
    private BigDecimal qteDeduitStock;
}
