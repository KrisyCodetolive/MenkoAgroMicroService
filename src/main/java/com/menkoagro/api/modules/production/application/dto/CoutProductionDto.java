package com.menkoagro.api.modules.production.application.dto;

import com.menkoagro.api.modules.production.domain.entity.CategorieCout;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Coût enregistré pour une production")
public class CoutProductionDto {

    @Schema(description = "Identifiant du coût", example = "ff0e8400-e29b-41d4-a716-446655440010")
    private UUID id;

    @Schema(description = "Catégorie du coût", example = "ALIMENTATION",
            allowableValues = {"INTRANTS", "MAIN_OEUVRE", "TRANSPORT", "VETERINAIRE", "ALIMENTATION", "AUTRE"})
    private CategorieCout categorie;

    @Schema(description = "Libellé descriptif", example = "Achat aliment démarrage 500kg")
    private String libelle;

    @Schema(description = "Montant du coût en FCFA", example = "125000.00")
    private BigDecimal montant;

    @Schema(description = "Date de la dépense", example = "2024-01-16")
    private LocalDate date;
}
