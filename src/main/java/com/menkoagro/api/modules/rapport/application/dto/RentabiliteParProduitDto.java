package com.menkoagro.api.modules.rapport.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Rentabilité calculée pour un produit sur la période")
public class RentabiliteParProduitDto {

    @Schema(description = "Identifiant du produit", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID produitId;

    @Schema(description = "Nom du produit", example = "Poulet de chair")
    private String nomProduit;

    @Schema(description = "Total des revenus issus des ventes (FCFA)", example = "1250000.00")
    private BigDecimal totalVentes;

    @Schema(description = "Total des coûts de production (FCFA)", example = "850000.00")
    private BigDecimal totalCouts;

    @Schema(description = "Marge brute = totalVentes - totalCouts (FCFA)", example = "400000.00")
    private BigDecimal marge;

    @Schema(description = "Taux de marge en pourcentage", example = "32.00")
    private BigDecimal tauxMarge;

    @Schema(description = "Nombre de ventes contenant ce produit", example = "15")
    private long nombreVentes;

    @Schema(description = "Nombre de productions terminées sur la période", example = "3")
    private long nombreProductions;
}
