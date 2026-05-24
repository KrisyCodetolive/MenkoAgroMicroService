package com.menkoagro.api.modules.rapport.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Schema(description = "Rapport de rentabilité global sur une période")
public class RentabiliteDto {

    @Schema(description = "Date de début de la période analysée", example = "2024-01-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin de la période analysée", example = "2024-12-31")
    private LocalDate dateFin;

    @Schema(description = "Total des revenus de vente sur la période (FCFA)", example = "5000000.00")
    private BigDecimal totalRevenus;

    @Schema(description = "Total des coûts de production sur la période (FCFA)", example = "3200000.00")
    private BigDecimal totalCouts;

    @Schema(description = "Marge nette = totalRevenus - totalCouts (FCFA)", example = "1800000.00")
    private BigDecimal margeNette;

    @Schema(description = "Taux de marge net en pourcentage", example = "36.00")
    private BigDecimal tauxMargePercent;

    @Schema(description = "Détail par produit")
    private List<RentabiliteParProduitDto> detail;
}
