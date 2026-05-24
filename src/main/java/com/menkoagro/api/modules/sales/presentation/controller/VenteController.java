package com.menkoagro.api.modules.sales.presentation.controller;

import com.menkoagro.api.modules.sales.application.dto.VenteDto;
import com.menkoagro.api.modules.sales.application.dto.VenteRequest;
import com.menkoagro.api.modules.sales.application.service.VenteService;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ventes")
@RequiredArgsConstructor
@Tag(name = "Ventes", description = "Enregistrement des ventes et téléchargement des reçus PDF")
@SecurityRequirement(name = "bearerAuth")
public class VenteController {

    private final VenteService venteService;

    @GetMapping
    @PreAuthorize("hasAuthority('VENTE_VOIR')")
    @Operation(summary = "Liste toutes les ventes", description = "Filtre optionnel par client (retourne les ventes triées par date décroissante)")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation VENTE_VOIR requise")
    })
    public ResponseEntity<ApiResponse<List<VenteDto>>> getAll(
            @Parameter(description = "Filtrer par client")
            @RequestParam(required = false) UUID clientId) {
        List<VenteDto> result = (clientId != null)
                ? venteService.getByClient(clientId)
                : venteService.getAll();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENTE_VOIR')")
    @Operation(summary = "Détail d'une vente")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vente trouvée",
                    content = @Content(schema = @Schema(implementation = VenteDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation VENTE_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vente introuvable")
    })
    public ResponseEntity<ApiResponse<VenteDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(venteService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('VENTE_CREER')")
    @Operation(
            summary = "Enregistrer une vente",
            description = "Décrémente automatiquement le stock (SORTIE/VENTE) et génère un reçu PDF. " +
                    "La quantité en stock est déduite selon quantite × conditionnement.quantiteBase."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Vente enregistrée — reçu PDF généré",
                    content = @Content(schema = @Schema(implementation = VenteDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides ou stock insuffisant"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation VENTE_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client, produit ou conditionnement introuvable")
    })
    public ResponseEntity<ApiResponse<VenteDto>> creer(@Valid @RequestBody VenteRequest request) {
        VenteDto dto = venteService.creerVente(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Vente enregistrée — reçu généré", dto));
    }

    @GetMapping("/{id}/recu/pdf")
    @PreAuthorize("hasAuthority('VENTE_VOIR')")
    @Operation(summary = "Télécharger le reçu PDF d'une vente",
            description = "Retourne le fichier PDF du reçu. Le nom du fichier est le numéro de reçu.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "PDF retourné",
                    content = @Content(mediaType = "application/pdf")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation VENTE_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vente ou reçu introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Erreur lors de la lecture du fichier PDF")
    })
    public ResponseEntity<byte[]> downloadRecu(@PathVariable UUID id) {
        byte[] pdfBytes = venteService.getRecuPdf(id);
        VenteDto vente = venteService.getById(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(vente.getRecu().getNumeroRecu() + ".pdf")
                .build());
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
