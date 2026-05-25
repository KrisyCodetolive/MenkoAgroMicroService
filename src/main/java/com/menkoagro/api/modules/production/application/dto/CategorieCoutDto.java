package com.menkoagro.api.modules.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Catégorie de coût de production")
public class CategorieCoutDto {

    @Schema(description = "Code à envoyer dans les requêtes API", example = "ALIMENTATION")
    private String code;

    @Schema(description = "Libellé lisible pour affichage", example = "Alimentation animale")
    private String libelle;
}