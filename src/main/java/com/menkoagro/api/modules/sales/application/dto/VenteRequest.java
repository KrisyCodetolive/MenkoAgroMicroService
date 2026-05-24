package com.menkoagro.api.modules.sales.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Données pour enregistrer une nouvelle vente")
public class VenteRequest {

    @NotNull(message = "Le client est obligatoire")
    @Schema(description = "Client acheteur", example = "bb0e8400-e29b-41d4-a716-446655440006",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID clientId;

    @NotEmpty(message = "La vente doit contenir au moins une ligne")
    @Valid
    @Schema(description = "Lignes de vente (au moins une)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<LigneVenteRequest> lignes;
}
