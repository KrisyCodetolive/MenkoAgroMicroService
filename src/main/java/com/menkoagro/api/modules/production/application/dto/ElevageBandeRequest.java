package com.menkoagro.api.modules.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Données pour démarrer une bande d'élevage")
public class ElevageBandeRequest {

    @NotNull(message = "Le produit est obligatoire")
    @Schema(description = "Produit issu de l'élevage (ex: poulet, œuf)", example = "660e8400-e29b-41d4-a716-446655440001",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID produitId;

    @NotNull(message = "La date de début est obligatoire")
    @Schema(description = "Date de mise en place de la bande", example = "2024-01-15",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;

    @Size(max = 50)
    @Schema(description = "Référence interne de la bande", example = "BANDE-2024-001")
    private String reference;

    @Min(value = 1, message = "Le nombre d'animaux doit être au moins 1")
    @Schema(description = "Nombre d'animaux en entrée de bande", example = "500")
    private Integer nombreAnimaux;

    @Size(max = 100)
    @Schema(description = "Espèce ou race des animaux", example = "Poulet Cobb 500")
    private String typeAnimal;
}
