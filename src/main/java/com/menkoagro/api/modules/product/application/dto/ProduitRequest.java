package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Données pour créer ou modifier un produit")
public class ProduitRequest {

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 150, message = "Le nom ne doit pas dépasser 150 caractères")
    @Schema(description = "Nom du produit", example = "Poulet de chair", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nom;

    @Schema(description = "Description optionnelle", example = "Race Cobb 500, alimentation maïs-soja")
    private String description;

    @NotBlank(message = "L'unité de base est obligatoire")
    @Size(max = 50)
    @Schema(description = "Unité de base pour le stock", example = "kg", defaultValue = "pièce",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String uniteBase = "pièce";

    @NotNull(message = "La catégorie est obligatoire")
    @Schema(description = "Identifiant de la catégorie", example = "990e8400-e29b-41d4-a716-446655440004",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID categorieId;

    @Valid
    @Schema(description = "Conditionnements initiaux à créer avec le produit (optionnel)")
    private List<ConditionnementRequest> conditionnements = new ArrayList<>();
}
