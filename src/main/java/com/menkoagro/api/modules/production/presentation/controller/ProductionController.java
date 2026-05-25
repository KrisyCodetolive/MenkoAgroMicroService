package com.menkoagro.api.modules.production.presentation.controller;

import com.menkoagro.api.modules.production.application.dto.CloturerProductionRequest;
import com.menkoagro.api.modules.production.application.dto.CategorieCoutDto;
import com.menkoagro.api.modules.production.application.dto.CoutProductionDto;
import com.menkoagro.api.modules.production.application.dto.CoutProductionRequest;
import com.menkoagro.api.modules.production.application.dto.ElevageBandeRequest;
import com.menkoagro.api.modules.production.application.dto.EtapeProductionDto;
import com.menkoagro.api.modules.production.application.dto.EtapeProductionRequest;
import com.menkoagro.api.modules.production.application.dto.ProductionAgricoleRequest;
import com.menkoagro.api.modules.production.application.dto.ProductionDto;
import com.menkoagro.api.modules.production.application.service.CoutProductionService;
import com.menkoagro.api.modules.production.application.service.EtapeProductionService;
import com.menkoagro.api.modules.production.application.service.ProductionService;
import com.menkoagro.api.modules.production.domain.entity.StatutProduction;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/productions")
@RequiredArgsConstructor
@Tag(name = "Production", description = "Gestion des productions agricoles et d'élevage")
@SecurityRequirement(name = "bearerAuth")
public class ProductionController {

    private final ProductionService productionService;
    private final EtapeProductionService etapeProductionService;
    private final CoutProductionService coutProductionService;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCTION_VOIR')")
    @Operation(summary = "Liste les productions", description = "Filtre optionnel par statut (EN_COURS, TERMINEE, ABANDONNEE) ou produitId")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_VOIR requise")
    })
    public ResponseEntity<ApiResponse<List<ProductionDto>>> getAll(
            @Parameter(description = "Filtrer par statut", example = "EN_COURS")
            @RequestParam(required = false) StatutProduction statut,
            @Parameter(description = "Filtrer par produit")
            @RequestParam(required = false) UUID produitId) {
        List<ProductionDto> result;
        if (statut != null) {
            result = productionService.getByStatut(statut);
        } else if (produitId != null) {
            result = productionService.getByProduit(produitId);
        } else {
            result = productionService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCTION_VOIR')")
    @Operation(summary = "Détail d'une production")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Production trouvée",
                    content = @Content(schema = @Schema(implementation = ProductionDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production introuvable")
    })
    public ResponseEntity<ApiResponse<ProductionDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(productionService.getById(id)));
    }

    @PostMapping("/agricoles")
    @PreAuthorize("hasAuthority('PRODUCTION_CREER')")
    @Operation(summary = "Créer une production agricole", description = "Démarre une campagne de culture avec zone et superficie")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Production agricole créée",
                    content = @Content(schema = @Schema(implementation = ProductionDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit introuvable")
    })
    public ResponseEntity<ApiResponse<ProductionDto>> creerAgricole(
            @Valid @RequestBody ProductionAgricoleRequest request) {
        ProductionDto dto = productionService.creerAgricole(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Production agricole créée", dto));
    }

    @PostMapping("/elevages")
    @PreAuthorize("hasAuthority('PRODUCTION_CREER')")
    @Operation(summary = "Créer une production d'élevage (bande)", description = "Démarre une bande d'élevage avec nombre d'animaux et type")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Production d'élevage créée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit introuvable")
    })
    public ResponseEntity<ApiResponse<ProductionDto>> creerElevage(
            @Valid @RequestBody ElevageBandeRequest request) {
        ProductionDto dto = productionService.creerElevage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Production d'élevage créée", dto));
    }

    @PatchMapping("/{id}/cloturer")
    @PreAuthorize("hasAuthority('PRODUCTION_CREER')")
    @Operation(summary = "Clôturer une production",
               description = "Marque la production TERMINEE et alimente le stock (ENTREE/PRODUCTION) avec la quantité produite")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Production clôturée — stock mis à jour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Production déjà terminée/abandonnée, ou données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production introuvable")
    })
    public ResponseEntity<ApiResponse<ProductionDto>> cloturer(
            @PathVariable UUID id,
            @Valid @RequestBody CloturerProductionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Production clôturée — stock mis à jour",
                productionService.cloturer(id, request)));
    }

    @PatchMapping("/{id}/abandonner")
    @PreAuthorize("hasAuthority('PRODUCTION_CREER')")
    @Operation(summary = "Abandonner une production", description = "Marque la production ABANDONNEE sans impact sur le stock")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Production abandonnée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Production déjà terminée/abandonnée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production introuvable")
    })
    public ResponseEntity<ApiResponse<ProductionDto>> abandonner(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Production abandonnée", productionService.abandonner(id)));
    }

    // ─── Étapes ──────────────────────────────────────────────────────────────────

    @GetMapping("/{id}/etapes")
    @PreAuthorize("hasAuthority('PRODUCTION_VOIR')")
    @Operation(summary = "Lister les étapes d'une production agricole")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des étapes"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Production non agricole"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production introuvable")
    })
    public ResponseEntity<ApiResponse<List<EtapeProductionDto>>> getEtapes(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(etapeProductionService.getEtapes(id)));
    }

    @PostMapping("/{id}/etapes")
    @PreAuthorize("hasAuthority('PRODUCTION_CREER')")
    @Operation(summary = "Ajouter une étape (AGRICOLE uniquement)",
            description = "Types : PREPARATION_SOL, PLANTATION, ENTRETIEN, RECOLTE")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Étape ajoutée",
                    content = @Content(schema = @Schema(implementation = EtapeProductionDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Type de production incompatible ou données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production introuvable")
    })
    public ResponseEntity<ApiResponse<EtapeProductionDto>> addEtape(
            @PathVariable UUID id,
            @Valid @RequestBody EtapeProductionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Étape ajoutée", etapeProductionService.addEtape(id, request)));
    }

    @PutMapping("/{id}/etapes/{etapeId}")
    @PreAuthorize("hasAuthority('PRODUCTION_CREER')")
    @Operation(summary = "Modifier une étape")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Étape mise à jour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production ou étape introuvable")
    })
    public ResponseEntity<ApiResponse<EtapeProductionDto>> updateEtape(
            @PathVariable UUID id,
            @PathVariable UUID etapeId,
            @Valid @RequestBody EtapeProductionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Étape mise à jour",
                etapeProductionService.updateEtape(id, etapeId, request)));
    }

    @DeleteMapping("/{id}/etapes/{etapeId}")
    @PreAuthorize("hasAuthority('PRODUCTION_CREER')")
    @Operation(summary = "Supprimer une étape")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Étape supprimée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_CREER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Étape introuvable")
    })
    public ResponseEntity<ApiResponse<Void>> deleteEtape(
            @PathVariable UUID id,
            @PathVariable UUID etapeId) {
        etapeProductionService.deleteEtape(id, etapeId);
        return ResponseEntity.ok(ApiResponse.ok("Étape supprimée", null));
    }

    // ─── Coûts ───────────────────────────────────────────────────────────────────

    @GetMapping("/couts/categories")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Lister les catégories de coût disponibles",
            description = "Retourne la liste des catégories utilisables dans un select (code + libellé)")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des catégories"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<List<CategorieCoutDto>>> getCategoriesCout() {
        return ResponseEntity.ok(ApiResponse.ok(coutProductionService.getCategoriesCout()));
    }

    @GetMapping("/{id}/couts")
    @PreAuthorize("hasAuthority('PRODUCTION_VOIR')")
    @Operation(summary = "Lister les coûts d'une production")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des coûts"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation PRODUCTION_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production introuvable")
    })
    public ResponseEntity<ApiResponse<List<CoutProductionDto>>> getCouts(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(coutProductionService.getCouts(id)));
    }

    @PostMapping("/{id}/couts")
    @PreAuthorize("hasAuthority('COUT_ENREGISTRER')")
    @Operation(summary = "Enregistrer un coût de production")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Coût enregistré",
                    content = @Content(schema = @Schema(implementation = CoutProductionDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation COUT_ENREGISTRER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Production introuvable")
    })
    public ResponseEntity<ApiResponse<CoutProductionDto>> addCout(
            @PathVariable UUID id,
            @Valid @RequestBody CoutProductionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Coût enregistré", coutProductionService.addCout(id, request)));
    }

    @DeleteMapping("/{id}/couts/{coutId}")
    @PreAuthorize("hasAuthority('COUT_ENREGISTRER')")
    @Operation(summary = "Supprimer un coût de production")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Coût supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation COUT_ENREGISTRER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Coût introuvable")
    })
    public ResponseEntity<ApiResponse<Void>> deleteCout(
            @PathVariable UUID id,
            @PathVariable UUID coutId) {
        coutProductionService.deleteCout(id, coutId);
        return ResponseEntity.ok(ApiResponse.ok("Coût supprimé", null));
    }
}
