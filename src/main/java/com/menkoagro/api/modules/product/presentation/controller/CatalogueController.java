package com.menkoagro.api.modules.product.presentation.controller;

import com.menkoagro.api.modules.product.application.dto.CategorieProduitDto;
import com.menkoagro.api.modules.product.application.dto.CategorieProduitRequest;
import com.menkoagro.api.modules.product.application.dto.TypeCategorieDto;
import com.menkoagro.api.modules.product.application.dto.TypeCategorieRequest;
import com.menkoagro.api.modules.product.application.service.CatalogueService;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalogue")
@RequiredArgsConstructor
@Tag(name = "Catalogue", description = "Types de catégorie et catégories de produits (référentiel)")
@SecurityRequirement(name = "bearerAuth")
public class CatalogueController {

    private final CatalogueService catalogueService;

    // ─── Types de catégorie ───────────────────────────────────────────────────────

    @GetMapping("/types")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Liste tous les types de catégorie", description = "Ex: AGRICOLE, ELEVAGE")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<List<TypeCategorieDto>>> getTypes() {
        return ResponseEntity.ok(ApiResponse.ok(catalogueService.getAllTypes()));
    }

    @PostMapping("/types")
    @PreAuthorize("hasAuthority('CATALOGUE_GERER')")
    @Operation(summary = "Créer un type de catégorie", description = "Réservé à l'administrateur")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Type créé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nom déjà utilisé")
    })
    public ResponseEntity<ApiResponse<TypeCategorieDto>> createType(@Valid @RequestBody TypeCategorieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Type de catégorie créé", catalogueService.createType(request)));
    }

    @PutMapping("/types/{id}")
    @PreAuthorize("hasAuthority('CATALOGUE_GERER')")
    @Operation(summary = "Modifier un type de catégorie")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Type modifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nom déjà utilisé")
    })
    public ResponseEntity<ApiResponse<TypeCategorieDto>> updateType(
            @PathVariable UUID id,
            @Valid @RequestBody TypeCategorieRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Type de catégorie modifié", catalogueService.updateType(id, request)));
    }

    @DeleteMapping("/types/{id}")
    @PreAuthorize("hasAuthority('CATALOGUE_GERER')")
    @Operation(summary = "Supprimer un type de catégorie", description = "Échoue si des catégories y sont rattachées")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Type supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Des catégories y sont rattachées")
    })
    public ResponseEntity<Void> deleteType(@PathVariable UUID id) {
        catalogueService.deleteType(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Catégories de produit ────────────────────────────────────────────────────

    @GetMapping("/categories")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Liste toutes les catégories de produits")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<List<CategorieProduitDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(catalogueService.getAllCategories()));
    }

    @GetMapping("/categories/type/{typeCategorieId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Catégories filtrées par type", description = "Ex: toutes les catégories de type ELEVAGE")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Catégories retournées"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type introuvable")
    })
    public ResponseEntity<ApiResponse<List<CategorieProduitDto>>> getCategoriesByType(
            @PathVariable UUID typeCategorieId) {
        return ResponseEntity.ok(ApiResponse.ok(catalogueService.getCategoriesByType(typeCategorieId)));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAuthority('CATALOGUE_GERER')")
    @Operation(summary = "Créer une catégorie de produit")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Catégorie créée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type de catégorie introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nom déjà utilisé dans ce type")
    })
    public ResponseEntity<ApiResponse<CategorieProduitDto>> createCategorie(
            @Valid @RequestBody CategorieProduitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Catégorie créée", catalogueService.createCategorie(request)));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('CATALOGUE_GERER')")
    @Operation(summary = "Modifier une catégorie de produit")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Catégorie modifiée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Catégorie introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Nom déjà utilisé dans ce type")
    })
    public ResponseEntity<ApiResponse<CategorieProduitDto>> updateCategorie(
            @PathVariable UUID id,
            @Valid @RequestBody CategorieProduitRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Catégorie modifiée", catalogueService.updateCategorie(id, request)));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAuthority('CATALOGUE_GERER')")
    @Operation(summary = "Supprimer une catégorie de produit", description = "Échoue si des produits y sont rattachés")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Catégorie supprimée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Catégorie introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Des produits sont rattachés à cette catégorie")
    })
    public ResponseEntity<Void> deleteCategorie(@PathVariable UUID id) {
        catalogueService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }
}