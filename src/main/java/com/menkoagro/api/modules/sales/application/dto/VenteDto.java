package com.menkoagro.api.modules.sales.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Vente avec ses lignes et le reçu PDF associé")
public class VenteDto {

    @Schema(description = "Identifiant de la vente", example = "110e8400-e29b-41d4-a716-446655440011")
    private UUID id;

    @Schema(description = "Identifiant du client acheteur", example = "bb0e8400-e29b-41d4-a716-446655440006")
    private UUID clientId;

    @Schema(description = "Nom du client", example = "Moussa Coulibaly")
    private String nomClient;

    @Schema(description = "Montant total de la vente en FCFA", example = "75000.00")
    private BigDecimal montantTotal;

    @Schema(description = "Date et heure de la vente", example = "2024-03-15T14:30:00")
    private LocalDateTime dateVente;

    @Schema(description = "Lignes de la vente (un produit par ligne)")
    private List<LigneVenteDto> lignes;

    @Schema(description = "Reçu PDF généré automatiquement")
    private RecuDto recu;
}
