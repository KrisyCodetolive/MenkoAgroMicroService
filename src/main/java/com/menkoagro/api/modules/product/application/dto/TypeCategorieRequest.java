package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Données pour créer ou modifier un type de catégorie")
public class TypeCategorieRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Schema(description = "Nom du type", example = "AQUACOLE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Schema(description = "Description du type", example = "Produits issus de l'aquaculture")
    private String description;
}