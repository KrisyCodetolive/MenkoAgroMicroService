package com.menkoagro.api.modules.stock.application.dto;

import com.menkoagro.api.modules.stock.domain.entity.MotifMouvement;
import com.menkoagro.api.modules.stock.domain.entity.TypeMouvement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Demande d'ajustement manuel du stock")
public class AjustementStockRequest {

    @NotNull(message = "Le type de mouvement est obligatoire (ENTREE ou SORTIE)")
    @Schema(description = "Direction de l'ajustement", example = "ENTREE", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"ENTREE", "SORTIE"})
    private TypeMouvement type;

    @NotNull(message = "La quantité est obligatoire")
    @DecimalMin(value = "0.001", message = "La quantité doit être supérieure à zéro")
    @Schema(description = "Quantité à ajouter ou retirer (en unité de base)", example = "200.000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal quantite;

    @NotNull(message = "Le motif est obligatoire")
    @Schema(description = "Motif de l'ajustement (ACHAT, PERTE ou AJUSTEMENT)", example = "ACHAT",
            requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"ACHAT", "PERTE", "AJUSTEMENT"})
    private MotifMouvement motif;

    @Schema(description = "Référence optionnelle (numéro de bon de commande...)", example = "BC-2024-042")
    private String reference;
}
