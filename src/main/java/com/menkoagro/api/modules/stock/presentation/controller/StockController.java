package com.menkoagro.api.modules.stock.presentation.controller;

import com.menkoagro.api.modules.stock.application.dto.AjustementStockRequest;
import com.menkoagro.api.modules.stock.application.dto.MouvementStockDto;
import com.menkoagro.api.modules.stock.application.dto.StockDto;
import com.menkoagro.api.modules.stock.application.service.StockService;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "Gestion des niveaux de stock et des mouvements")
@SecurityRequirement(name = "bearerAuth")
public class StockController {

    private final StockService stockService;

    @GetMapping
    @PreAuthorize("hasAuthority('STOCK_VOIR')")
    @Operation(summary = "Liste tous les stocks")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_VOIR requise")
    })
    public ResponseEntity<ApiResponse<List<StockDto>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(stockService.getAll()));
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAuthority('STOCK_VOIR')")
    @Operation(summary = "Stocks sous le seuil d'alerte", description = "Retourne les produits dont la quantité est <= seuil d'alerte")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste des stocks en alerte"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_VOIR requise")
    })
    public ResponseEntity<ApiResponse<List<StockDto>>> getAlertes() {
        return ResponseEntity.ok(ApiResponse.ok(stockService.getStocksEnAlerte()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STOCK_VOIR')")
    @Operation(summary = "Détail d'un stock par son ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock trouvé",
                    content = @Content(schema = @Schema(implementation = StockDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Stock introuvable")
    })
    public ResponseEntity<ApiResponse<StockDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(stockService.getById(id)));
    }

    @GetMapping("/produit/{produitId}")
    @PreAuthorize("hasAuthority('STOCK_VOIR')")
    @Operation(summary = "Stock d'un produit", description = "Récupère le stock associé à un produit donné")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock trouvé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Produit ou stock introuvable")
    })
    public ResponseEntity<ApiResponse<StockDto>> getByProduit(@PathVariable UUID produitId) {
        return ResponseEntity.ok(ApiResponse.ok(stockService.getByProduitId(produitId)));
    }

    @GetMapping("/{id}/mouvements")
    @PreAuthorize("hasAuthority('STOCK_VOIR')")
    @Operation(summary = "Historique des mouvements d'un stock", description = "Retourne tous les mouvements triés par date décroissante")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historique retourné"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Stock introuvable")
    })
    public ResponseEntity<ApiResponse<List<MouvementStockDto>>> getMouvements(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(stockService.getMouvements(id)));
    }

    @PostMapping("/{id}/ajustement")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Ajustement manuel du stock",
            description = "Motifs autorisés pour un ajustement manuel : ACHAT, PERTE, AJUSTEMENT")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock ajusté",
                    content = @Content(schema = @Schema(implementation = StockDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides (quantité négative, motif interdit)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Stock introuvable")
    })
    public ResponseEntity<ApiResponse<StockDto>> ajuster(
            @PathVariable UUID id,
            @Valid @RequestBody AjustementStockRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Stock ajusté", stockService.ajuster(id, request)));
    }

    @PatchMapping("/{id}/seuil")
    @PreAuthorize("hasAuthority('STOCK_MODIFIER')")
    @Operation(summary = "Modifier le seuil d'alerte d'un stock")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Seuil mis à jour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Seuil négatif"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation STOCK_MODIFIER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Stock introuvable")
    })
    public ResponseEntity<ApiResponse<StockDto>> mettreAJourSeuil(
            @PathVariable UUID id,
            @Parameter(description = "Nouveau seuil d'alerte (>= 0)", example = "50.0")
            @RequestParam @DecimalMin(value = "0", message = "Le seuil doit être >= 0") BigDecimal seuilAlerte) {
        return ResponseEntity.ok(ApiResponse.ok("Seuil mis à jour", stockService.mettreAJourSeuil(id, seuilAlerte)));
    }
}
