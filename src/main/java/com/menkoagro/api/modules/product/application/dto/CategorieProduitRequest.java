package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Données pour créer ou modifier une catégorie de produit")
public class CategorieProduitRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Schema(description = "Nom de la catégorie", example = "Aquaculture d'eau douce", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Schema(description = "Description de la catégorie", example = "Poissons et crustacés d'eau douce")
    private String description;

    @NotNull(message = "Le type de catégorie est obligatoire")
    @Schema(description = "Identifiant du type de catégorie parent", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID typeCategorieId;
}