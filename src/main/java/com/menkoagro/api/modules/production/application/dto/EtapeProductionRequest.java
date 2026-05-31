package com.menkoagro.api.modules.production.application.dto;

import com.menkoagro.api.modules.production.domain.entity.TypeEtape;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Données pour ajouter une étape à une production agricole")
public class EtapeProductionRequest {

    @NotNull(message = "Le type d'étape est obligatoire")
    @Schema(description = "Type d'étape", example = "PREPARATION_SOL", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"PREPARATION_SOL", "PLANTATION", "ENTRETIEN", "RECOLTE"})
    private TypeEtape type;

    @Schema(description = "Date de réalisation (null si l'étape n'est pas encore effectuée)", example = "2024-01-18")
    private LocalDate dateRealisation;

    @Schema(description = "Notes ou observations libres", example = "Utilisation de charrue à disques")
    private String notes;
}
