package com.menkoagro.api.modules.product.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Données pour créer ou modifier un conditionnement")
public class ConditionnementRequest {

    @NotBlank(message = "Le libellé est obligatoire")
    @Schema(description = "Libellé du conditionnement", example = "Sac 50kg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String libelle;

    @NotNull(message = "La quantité de base est obligatoire")
    @DecimalMin(value = "0.001", message = "La quantité de base doit être supérieure à 0")
    @Schema(description = "Quantité en unité de base contenue dans ce conditionnement", example = "1.500",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal quantiteBase;

    @NotNull(message = "Le prix de vente est obligatoire")
    @DecimalMin(value = "0", inclusive = true, message = "Le prix de vente ne peut pas être négatif")
    @Schema(description = "Prix de vente en FCFA", example = "15000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal prixVente;

    @Schema(description = "Définir comme conditionnement par défaut du produit", example = "false")
    private boolean estParDefaut = false;
}
