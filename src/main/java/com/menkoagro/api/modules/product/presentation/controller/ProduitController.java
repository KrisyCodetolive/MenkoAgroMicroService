package com.menkoagro.api.modules.product.presentation.controller;

import com.menkoagro.api.modules.product.application.dto.ConditionnementDto;
import com.menkoagro.api.modules.product.application.dto.ConditionnementRequest;
import com.menkoagro.api.modules.product.application.dto.ProduitDto;
import com.menkoagro.api.modules.product.application.dto.ProduitRequest;
import com.menkoagro.api.modules.product.application.service.ProduitService;
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
@RequestMapping("/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "CRUD produits et gestion des conditionnements")
@SecurityRequirement(name = "bearerAuth")
public class ProduitController {

    private final ProduitService produitService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Liste tous les produits", description = "Filtre optionnel par catégorie")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<List<ProduitDto>>> getAll(
            @Parameter(description = "Filtrer par ID de catégorie")
            @RequestParam(required = false) UUID categorieId) {
        List<ProduitDto> result = (categorieId != null)
                ? produitService.getByCategorie(categorieId)
                : produitService.getAll();
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Détail d'un produit")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produit trouvé",
                    content = @Content(schema = @Schema(implementation = ProduitDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit introuvable")
    })
    public ResponseEntity<ApiResponse<ProduitDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(produitService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Créer un produit", description = "Crée automatiquement un stock initial à zéro associé au produit")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Produit créé avec son stock initial",
                    content = @Content(schema = @Schema(implementation = ProduitDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Produit déjà existant (contrainte d'unicité)")
    })
    public ResponseEntity<ApiResponse<ProduitDto>> create(@Valid @RequestBody ProduitRequest request) {
        ProduitDto dto = produitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Produit créé", dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Modifier un produit")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produit mis à jour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit introuvable")
    })
    public ResponseEntity<ApiResponse<ProduitDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProduitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Produit mis à jour", produitService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Supprimer un produit")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Produit supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Produit lié à des ventes ou productions existantes")
    })
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        produitService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Produit supprimé", null));
    }

    // ─── Conditionnements ────────────────────────────────────────────────────────

    @PostMapping("/{id}/conditionnements")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Ajouter un conditionnement à un produit",
            description = "Définit une unité de vente (ex: sac 50kg, plateau 30 œufs) avec son prix")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Conditionnement ajouté",
                    content = @Content(schema = @Schema(implementation = ConditionnementDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit introuvable")
    })
    public ResponseEntity<ApiResponse<ConditionnementDto>> addConditionnement(
            @PathVariable UUID id,
            @Valid @RequestBody ConditionnementRequest request) {
        ConditionnementDto dto = produitService.addConditionnement(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Conditionnement ajouté", dto));
    }

    @PutMapping("/{id}/conditionnements/{condId}")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Modifier un conditionnement")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conditionnement mis à jour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit ou conditionnement introuvable")
    })
    public ResponseEntity<ApiResponse<ConditionnementDto>> updateConditionnement(
            @PathVariable UUID id,
            @PathVariable UUID condId,
            @Valid @RequestBody ConditionnementRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Conditionnement mis à jour",
                produitService.updateConditionnement(id, condId, request)));
    }

    @DeleteMapping("/{id}/conditionnements/{condId}")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Supprimer un conditionnement")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conditionnement supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conditionnement introuvable")
    })
    public ResponseEntity<ApiResponse<Void>> deleteConditionnement(
            @PathVariable UUID id,
            @PathVariable UUID condId) {
        produitService.deleteConditionnement(id, condId);
        return ResponseEntity.ok(ApiResponse.ok("Conditionnement supprimé", null));
    }

    @PatchMapping("/{id}/conditionnements/{condId}/defaut")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Définir le conditionnement par défaut",
            description = "Met estParDefaut=true sur ce conditionnement et false sur les autres du même produit")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conditionnement par défaut défini"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Conditionnement introuvable")
    })
    public ResponseEntity<ApiResponse<ConditionnementDto>> setParDefaut(
            @PathVariable UUID id,
            @PathVariable UUID condId) {
        return ResponseEntity.ok(ApiResponse.ok("Conditionnement par défaut défini",
                produitService.setConditionnementParDefaut(id, condId)));
    }
}
