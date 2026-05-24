package com.menkoagro.api.modules.production.application.dto;

import com.menkoagro.api.modules.production.domain.entity.TypeEtape;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Étape d'une production agricole")
public class EtapeProductionDto {

    @Schema(description = "Identifiant de l'étape", example = "ee0e8400-e29b-41d4-a716-446655440009")
    private UUID id;

    @Schema(description = "Type d'étape agricole", example = "PLANTATION",
            allowableValues = {"PREPARATION_SOL", "PLANTATION", "ENTRETIEN", "RECOLTE"})
    private TypeEtape type;

    @Schema(description = "Date de réalisation de l'étape", example = "2024-01-20")
    private LocalDate dateRealisation;

    @Schema(description = "Notes ou observations sur l'étape", example = "Sol amendé avec fumier compostée")
    private String notes;
}
