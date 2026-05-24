package com.menkoagro.api.modules.customer.application.dto;

import com.menkoagro.api.modules.customer.domain.entity.CanalNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Demande d'envoi de notification à un client")
public class NotificationRequest {

    @NotNull(message = "Le canal est obligatoire (SMS ou EMAIL)")
    @Schema(description = "Canal d'envoi — SMS nécessite un téléphone, EMAIL nécessite un email",
            example = "SMS", requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"SMS", "EMAIL"})
    private CanalNotification canal;

    @Size(max = 200, message = "L'objet ne doit pas dépasser 200 caractères")
    @Schema(description = "Objet du message (optionnel, recommandé pour EMAIL)", example = "Relance commande")
    private String objet;

    @NotBlank(message = "Le contenu est obligatoire")
    @Schema(description = "Corps du message", example = "Bonjour, votre commande est prête à être retirée.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String contenu;
}
