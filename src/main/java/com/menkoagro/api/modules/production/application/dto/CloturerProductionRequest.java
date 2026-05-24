package com.menkoagro.api.modules.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Données pour clôturer une production et alimenter le stock")
public class CloturerProductionRequest {

    @NotNull(message = "La quantité produite est obligatoire")
    @DecimalMin(value = "0.001", message = "La quantité produite doit être supérieure à zéro")
    @Schema(description = "Quantité récoltée ou produite (en unité de base du produit), crédite le stock",
            example = "480.000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal quantiteProduite;

    @Schema(description = "Date de clôture effective (défaut : aujourd'hui)", example = "2024-03-10")
    private LocalDate dateFin;

    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "100", inclusive = true)
    @Schema(description = "(ELEVAGE uniquement) Taux de mortalité constaté en % (0–100)", example = "4.00")
    private BigDecimal tauxMortalite;
}
