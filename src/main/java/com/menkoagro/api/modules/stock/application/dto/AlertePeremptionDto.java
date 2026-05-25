package com.menkoagro.api.modules.stock.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Alerte de péremption pour un produit périssable")
public class AlertePeremptionDto {

    @Schema(description = "Identifiant du stock", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID stockId;

    @Schema(description = "Identifiant du produit", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID produitId;

    @Schema(description = "Nom du produit", example = "Gombo")
    private String nomProduit;

    @Schema(description = "Quantité disponible en stock", example = "120.000")
    private BigDecimal quantite;

    @Schema(description = "Unité de mesure", example = "kg")
    private String uniteBase;

    @Schema(description = "Durée de conservation définie sur le produit", example = "3")
    private Integer dureeConservationJours;

    @Schema(description = "Date de la dernière entrée en stock (production ou achat)", example = "2024-05-20")
    private LocalDate dernierEntreeDate;

    @Schema(description = "Date d'expiration estimée (dernierEntreeDate + dureeConservationJours)", example = "2024-05-23")
    private LocalDate dateExpirationEstimee;

    @Schema(description = "Nombre de jours restants avant expiration (négatif = déjà expiré)", example = "1")
    private long joursRestants;

    @Schema(description = "true si le produit est déjà expiré", example = "false")
    private boolean estExpire;
}