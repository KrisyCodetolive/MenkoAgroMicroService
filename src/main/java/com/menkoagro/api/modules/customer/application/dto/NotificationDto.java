package com.menkoagro.api.modules.customer.application.dto;

import com.menkoagro.api.modules.customer.domain.entity.CanalNotification;
import com.menkoagro.api.modules.customer.domain.entity.StatutNotification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Notification envoyée ou programmée pour un client")
public class NotificationDto {

    @Schema(description = "Identifiant de la notification", example = "cc0e8400-e29b-41d4-a716-446655440007")
    private UUID id;

    @Schema(description = "Identifiant du client destinataire", example = "bb0e8400-e29b-41d4-a716-446655440006")
    private UUID clientId;

    @Schema(description = "Nom du client destinataire", example = "Moussa Coulibaly")
    private String nomClient;

    @Schema(description = "Canal d'envoi", example = "SMS", allowableValues = {"SMS", "EMAIL"})
    private CanalNotification canal;

    @Schema(description = "Objet du message (pertinent pour EMAIL)", example = "Disponibilité poulets de chair")
    private String objet;

    @Schema(description = "Contenu du message", example = "Bonjour, nous avons du stock disponible. Contactez-nous.")
    private String contenu;

    @Schema(description = "Statut d'envoi", example = "EN_ATTENTE", allowableValues = {"EN_ATTENTE", "ENVOYE", "ECHEC"})
    private StatutNotification statut;

    @Schema(description = "Date et heure d'envoi effectif (null si EN_ATTENTE)", example = "2024-03-15T11:00:00")
    private LocalDateTime envoyeA;
}
