package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Fiche d'un produit avec ses conditionnements")
public class ProduitDto {

    @Schema(description = "Identifiant du produit", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "Nom du produit", example = "Poulet de chair")
    private String nom;

    @Schema(description = "Description détaillée", example = "Poulet élevé en bande de 500 sujets")
    private String description;

    @Schema(description = "Unité de mesure de base du stock", example = "kg")
    private String uniteBase;

    @Schema(description = "Identifiant de la catégorie", example = "990e8400-e29b-41d4-a716-446655440004")
    private UUID categorieId;

    @Schema(description = "Nom de la catégorie", example = "Volaille")
    private String nomCategorie;

    @Schema(description = "Type de catégorie parent", example = "ELEVAGE")
    private String typeCategorie;

    @Schema(description = "true si le produit est périssable", example = "true")
    private boolean estPerissable;

    @Schema(description = "Durée de conservation en jours après entrée en stock (null si non périssable)", example = "3")
    private Integer dureeConservationJours;

    @Schema(description = "Liste des conditionnements disponibles pour ce produit")
    private List<ConditionnementDto> conditionnements;

    @Schema(description = "Date de création", example = "2024-01-10T08:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière modification", example = "2024-03-01T14:30:00")
    private LocalDateTime updatedAt;
}
