package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Conditionnement d'un produit (unité de vente avec prix)")
public class ConditionnementDto {

    @Schema(description = "Identifiant du conditionnement", example = "aa0e8400-e29b-41d4-a716-446655440005")
    private UUID id;

    @Schema(description = "Libellé du conditionnement", example = "Sac 50kg")
    private String libelle;

    @Schema(description = "Quantité exprimée en unité de base (ex: 50 pour un sac de 50kg)", example = "50")
    private Integer quantiteBase;

    @Schema(description = "Prix de vente de ce conditionnement en FCFA", example = "15000.00")
    private BigDecimal prixVente;

    @Schema(description = "true si c'est le conditionnement par défaut du produit", example = "true")
    private boolean estParDefaut;
}
