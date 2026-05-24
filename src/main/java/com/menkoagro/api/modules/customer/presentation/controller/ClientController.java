package com.menkoagro.api.modules.customer.presentation.controller;

import com.menkoagro.api.modules.customer.application.dto.ClientDto;
import com.menkoagro.api.modules.customer.application.dto.ClientRequest;
import com.menkoagro.api.modules.customer.application.dto.NotificationDto;
import com.menkoagro.api.modules.customer.application.dto.NotificationRequest;
import com.menkoagro.api.modules.customer.application.service.ClientService;
import com.menkoagro.api.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Gestion des clients et notifications")
@SecurityRequirement(name = "bearerAuth")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @PreAuthorize("hasAuthority('CLIENT_VOIR')")
    @Operation(summary = "Liste tous les clients", description = "Filtre optionnel par nom, téléphone ou email (recherche insensible à la casse)")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Liste retournée"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation CLIENT_VOIR requise")
    })
    public ResponseEntity<ApiResponse<List<ClientDto>>> getAll(
            @Parameter(description = "Recherche dans nom, téléphone, email", example = "Coulibaly")
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.ok(clientService.search(search)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_VOIR')")
    @Operation(summary = "Détail d'un client")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Client trouvé",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation CLIENT_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<ApiResponse<ClientDto>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(clientService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT_GERER')")
    @Operation(summary = "Créer un client")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Client créé",
                    content = @Content(schema = @Schema(implementation = ClientDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation CLIENT_GERER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Email déjà utilisé")
    })
    public ResponseEntity<ApiResponse<ClientDto>> create(@Valid @RequestBody ClientRequest request) {
        ClientDto dto = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Client créé", dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_GERER')")
    @Operation(summary = "Modifier un client")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Client mis à jour"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation CLIENT_GERER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<ApiResponse<ClientDto>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Client mis à jour", clientService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CLIENT_GERER')")
    @Operation(summary = "Supprimer un client")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Client supprimé"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation CLIENT_GERER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client introuvable"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Client lié à des ventes existantes")
    })
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        clientService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Client supprimé", null));
    }

    // ─── Notifications ───────────────────────────────────────────────────────────

    @GetMapping("/{id}/notifications")
    @PreAuthorize("hasAuthority('CLIENT_VOIR')")
    @Operation(summary = "Historique des notifications d'un client")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historique retourné"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation CLIENT_VOIR requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getNotifications(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(clientService.getNotifications(id)));
    }

    @PostMapping("/{id}/notifications")
    @PreAuthorize("hasAuthority('CLIENT_GERER')")
    @Operation(summary = "Envoyer une notification à un client",
               description = "Canal SMS : nécessite un téléphone. Canal EMAIL : nécessite un email.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Notification enregistrée",
                    content = @Content(schema = @Schema(implementation = NotificationDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Données invalides ou canal sans coordonnées"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Non authentifié"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Autorisation CLIENT_GERER requise"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Client introuvable")
    })
    public ResponseEntity<ApiResponse<NotificationDto>> envoyerNotification(
            @PathVariable UUID id,
            @Valid @RequestBody NotificationRequest request) {
        NotificationDto dto = clientService.envoyerNotification(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Notification envoyée", dto));
    }
}
