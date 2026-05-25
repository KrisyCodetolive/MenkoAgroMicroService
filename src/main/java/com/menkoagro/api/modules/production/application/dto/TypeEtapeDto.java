package com.menkoagro.api.modules.production.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Type d'étape de production agricole")
public class TypeEtapeDto {

    @Schema(description = "Code à envoyer dans les requêtes API", example = "PLANTATION")
    private String code;

    @Schema(description = "Libellé lisible pour affichage", example = "Plantation")
    private String libelle;
}