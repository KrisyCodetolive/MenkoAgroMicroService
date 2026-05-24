package com.menkoagro.api.modules.stock.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Niveau de stock d'un produit")
public class StockDto {

    @Schema(description = "Identifiant du stock", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Identifiant du produit associé", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID produitId;

    @Schema(description = "Nom du produit", example = "Poulet de chair")
    private String nomProduit;

    @Schema(description = "Unité de mesure du stock", example = "kg")
    private String uniteBase;

    @Schema(description = "Quantité disponible en stock", example = "1250.500")
    private BigDecimal quantite;

    @Schema(description = "Seuil en dessous duquel une alerte est déclenchée", example = "100.000")
    private BigDecimal seuilAlerte;

    @Schema(description = "true si quantité <= seuil d'alerte", example = "false")
    private boolean sousAlerte;

    @Schema(description = "Date et heure de la dernière mise à jour", example = "2024-03-15T10:30:00")
    private LocalDateTime updatedAt;
}
