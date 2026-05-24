package com.menkoagro.api.modules.production.application.dto;

import com.menkoagro.api.modules.production.domain.entity.CategorieCout;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Données pour enregistrer un coût de production")
public class CoutProductionRequest {

    @NotNull(message = "La catégorie de coût est obligatoire")
    @Schema(description = "Catégorie du coût", example = "ALIMENTATION", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"INTRANTS", "MAIN_OEUVRE", "TRANSPORT", "VETERINAIRE", "ALIMENTATION", "AUTRE"})
    private CategorieCout categorie;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 200)
    @Schema(description = "Libellé descriptif de la dépense", example = "Achat aliment démarrage 500kg",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String libelle;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0", inclusive = true, message = "Le montant ne peut pas être négatif")
    @Schema(description = "Montant en FCFA (>= 0)", example = "125000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal montant;

    @NotNull(message = "La date est obligatoire")
    @Schema(description = "Date de la dépense", example = "2024-01-16", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;
}
