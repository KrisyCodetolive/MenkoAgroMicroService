package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@Schema(description = "Catégorie de produit (ex: Volaille, Cultures maraîchères)")
public class CategorieProduitDto {

    @Schema(description = "Identifiant de la catégorie", example = "990e8400-e29b-41d4-a716-446655440004")
    private UUID id;

    @Schema(description = "Nom de la catégorie", example = "Volaille")
    private String nom;

    @Schema(description = "Description de la catégorie", example = "Poulets, pintades, dindons...")
    private String description;

    @Schema(description = "Identifiant du type de catégorie parent", example = "880e8400-e29b-41d4-a716-446655440003")
    private UUID typeCategorieId;

    @Schema(description = "Nom du type de catégorie parent", example = "ELEVAGE")
    private String nomTypeCategorie;
}
