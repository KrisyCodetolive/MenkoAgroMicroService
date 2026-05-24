package com.menkoagro.api.modules.rapport.presentation.controller;

import com.menkoagro.api.modules.rapport.application.dto.RentabiliteDto;
import com.menkoagro.api.modules.rapport.application.service.RapportService;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/rapport")
@RequiredArgsConstructor
@Tag(name = "Rapport", description = "Rapports de rentabilité et d'analyse financière")
@SecurityRequirement(name = "bearerAuth")
public class RapportController {

    private final RapportService rapportService;

    @GetMapping("/rentabilite")
    @PreAuthorize("hasAuthority('RAPPORT_VOIR')")
    @Operation(
            summary = "Rapport de rentabilité",
            description = "Calcule les revenus de vente et les coûts de production par produit sur une période donnée. " +
                    "Seules les productions TERMINÉES dont la date de fin est dans la période sont prises en compte."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rapport calculé",
                    content = @Content(schema = @Schema(implementation = RentabiliteDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Paramètres de date invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation RAPPORT_VOIR requise")
    })
    public ResponseEntity<ApiResponse<RentabiliteDto>> getRentabilite(
            @Parameter(description = "Date de début (ISO date)", example = "2024-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin (ISO date)", example = "2024-12-31", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return ResponseEntity.ok(ApiResponse.ok(rapportService.calculerRentabilite(dateDebut, dateFin)));
    }
}
