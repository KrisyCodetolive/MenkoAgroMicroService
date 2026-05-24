package com.menkoagro.api.modules.stock.application.dto;

import com.menkoagro.api.modules.stock.domain.entity.MotifMouvement;
import com.menkoagro.api.modules.stock.domain.entity.TypeMouvement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Mouvement de stock (entrée ou sortie)")
public class MouvementStockDto {

    @Schema(description = "Identifiant du mouvement", example = "770e8400-e29b-41d4-a716-446655440002")
    private UUID id;

    @Schema(description = "Identifiant du stock concerné", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID stockId;

    @Schema(description = "Direction du mouvement", example = "ENTREE", allowableValues = {"ENTREE", "SORTIE"})
    private TypeMouvement type;

    @Schema(description = "Quantité du mouvement (en unité de base)", example = "500.000")
    private BigDecimal quantite;

    @Schema(description = "Motif du mouvement", example = "VENTE",
            allowableValues = {"PRODUCTION", "VENTE", "PERTE", "ACHAT", "AJUSTEMENT"})
    private MotifMouvement motif;

    @Schema(description = "Référence optionnelle (numéro de vente, production...)", example = "VENTE-ABCD1234")
    private String reference;

    @Schema(description = "Date et heure du mouvement", example = "2024-03-15T14:22:00")
    private LocalDateTime date;
}
