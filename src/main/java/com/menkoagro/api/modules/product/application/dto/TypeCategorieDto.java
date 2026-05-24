package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@Schema(description = "Type de catégorie de produit (AGRICOLE ou ELEVAGE)")
public class TypeCategorieDto {

    @Schema(description = "Identifiant du type", example = "880e8400-e29b-41d4-a716-446655440003")
    private UUID id;

    @Schema(description = "Nom du type", example = "AGRICOLE", allowableValues = {"AGRICOLE", "ELEVAGE"})
    private String nom;

    @Schema(description = "Description du type", example = "Produits issus des cultures")
    private String description;
}
