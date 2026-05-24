package com.menkoagro.api.modules.product.presentation.controller;

import com.menkoagro.api.modules.product.application.dto.CategorieProduitDto;
import com.menkoagro.api.modules.product.application.dto.TypeCategorieDto;
import com.menkoagro.api.modules.product.application.service.ProduitService;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/catalogue")
@RequiredArgsConstructor
@Tag(name = "Catalogue", description = "Types de catégorie et catégories de produits (référentiel)")
@SecurityRequirement(name = "bearerAuth")
public class CatalogueController {

    private final ProduitService produitService;

    @GetMapping("/types")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Liste tous les types de catégorie", description = "Valeurs possibles : AGRICOLE, ELEVAGE")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<List<TypeCategorieDto>>> getTypes() {
        return ResponseEntity.ok(ApiResponse.ok(produitService.getAllTypes()));
    }

    @GetMapping("/categories")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Liste toutes les catégories de produits")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<ApiResponse<List<CategorieProduitDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(produitService.getAllCategories()));
    }

    @GetMapping("/categories/type/{typeCategorieId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Catégories filtrées par type", description = "Ex: toutes les catégories de type ELEVAGE")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Catégories retournées"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Type de catégorie introuvable")
    })
    public ResponseEntity<ApiResponse<List<CategorieProduitDto>>> getCategoriesByType(
            @PathVariable UUID typeCategorieId) {
        return ResponseEntity.ok(ApiResponse.ok(produitService.getCategoriesByType(typeCategorieId)));
    }
}
