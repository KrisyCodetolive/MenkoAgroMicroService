package com.menkoagro.api.modules.production.application.dto;

import com.menkoagro.api.modules.production.domain.entity.StatutProduction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Fiche de production (agricole ou élevage)")
public class ProductionDto {

    @Schema(description = "Identifiant de la production", example = "dd0e8400-e29b-41d4-a716-446655440008")
    private UUID id;

    @Schema(description = "Type de production", example = "ELEVAGE", allowableValues = {"AGRICOLE", "ELEVAGE"})
    private String type;

    @Schema(description = "Identifiant du produit associé", example = "660e8400-e29b-41d4-a716-446655440001")
    private UUID produitId;

    @Schema(description = "Nom du produit", example = "Poulet de chair")
    private String nomProduit;

    @Schema(description = "Date de démarrage de la production", example = "2024-01-15")
    private LocalDate dateDebut;

    @Schema(description = "Date de clôture (null si EN_COURS)", example = "2024-03-10")
    private LocalDate dateFin;

    @Schema(description = "Statut de la production", example = "TERMINEE",
            allowableValues = {"EN_COURS", "TERMINEE", "ABANDONNEE"})
    private StatutProduction statut;

    @Schema(description = "Cumul de tous les coûts de production (FCFA)", example = "850000.00")
    private BigDecimal coutTotal;

    // Champs AGRICOLE
    @Schema(description = "(AGRICOLE) Zone géographique de la culture", example = "Zone Nord — Ferme A")
    private String zone;

    @Schema(description = "(AGRICOLE) Superficie de la parcelle en hectares", example = "2.50")
    private BigDecimal superficieParcelle;

    @Schema(description = "(AGRICOLE) Étapes de la campagne agricole")
    private List<EtapeProductionDto> etapes;

    // Champs ELEVAGE
    @Schema(description = "(ELEVAGE) Référence de la bande", example = "BANDE-2024-001")
    private String reference;

    @Schema(description = "(ELEVAGE) Nombre d'animaux en entrée de bande", example = "500")
    private Integer nombreAnimaux;

    @Schema(description = "(ELEVAGE) Espèce ou race", example = "Poulet Cobb 500")
    private String typeAnimal;

    @Schema(description = "(ELEVAGE) Taux de mortalité constaté en %", example = "3.50")
    private BigDecimal tauxMortalite;

    @Schema(description = "Coûts enregistrés pour cette production")
    private List<CoutProductionDto> couts;

    @Schema(description = "Date de création de l'enregistrement", example = "2024-01-15T07:30:00")
    private LocalDateTime createdAt;
}
