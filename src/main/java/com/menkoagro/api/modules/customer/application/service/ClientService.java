package com.menkoagro.api.modules.customer.application.service;

import com.menkoagro.api.modules.customer.application.dto.ClientDto;
import com.menkoagro.api.modules.customer.application.dto.ClientRequest;
import com.menkoagro.api.modules.customer.application.dto.NotificationDto;
import com.menkoagro.api.modules.customer.application.dto.NotificationRequest;
import com.menkoagro.api.modules.customer.domain.entity.CanalNotification;
import com.menkoagro.api.modules.customer.domain.entity.Client;
import com.menkoagro.api.modules.customer.domain.entity.Notification;
import com.menkoagro.api.modules.customer.domain.entity.StatutNotification;
import com.menkoagro.api.modules.customer.domain.repository.ClientRepository;
import com.menkoagro.api.modules.customer.domain.repository.NotificationRepository;
import com.menkoagro.api.shared.exception.BusinessException;
import com.menkoagro.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;
    private final NotificationRepository notificationRepository;

    // ─── Clients ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ClientDto> getAll() {
        return clientRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClientDto> search(String query) {
        if (query == null || query.isBlank()) {
            return getAll();
        }
        return clientRepository.searchByNomOrTelephoneOrEmail(query.trim()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientDto getById(UUID id) {
        return toDto(findClientById(id));
    }

    @Transactional
    public ClientDto create(ClientRequest request) {
        Client client = Client.builder()
                .nom(request.getNom())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .adresse(request.getAdresse())
                .build();
        return toDto(clientRepository.save(client));
    }

    @Transactional
    public ClientDto update(UUID id, ClientRequest request) {
        Client client = findClientById(id);
        client.setNom(request.getNom());
        client.setTelephone(request.getTelephone());
        client.setEmail(request.getEmail());
        client.setAdresse(request.getAdresse());
        return toDto(clientRepository.save(client));
    }

    @Transactional
    public void delete(UUID id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
    }

    // ─── Notifications ───────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotifications(UUID clientId) {
        findClientById(clientId);
        return notificationRepository.findByClientIdOrderByIdDesc(clientId).stream()
                .map(this::toNotificationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationDto envoyerNotification(UUID clientId, NotificationRequest request) {
        Client client = findClientById(clientId);

        validerCanalClient(client, request.getCanal());

        Notification notification = Notification.builder()
                .client(client)
                .canal(request.getCanal())
                .objet(request.getObjet())
                .contenu(request.getContenu())
                .statut(StatutNotification.EN_ATTENTE)
                .build();

        // Simulation de l'envoi — en production, appel API SMS/email ici
        try {
            log.info("Envoi {} à {} ({})", request.getCanal(),
                    client.getNom(), contactPour(client, request.getCanal()));
            notification.setStatut(StatutNotification.ENVOYE);
            notification.setEnvoyeA(LocalDateTime.now());
        } catch (Exception e) {
            log.error("Échec envoi notification : {}", e.getMessage());
            notification.setStatut(StatutNotification.ECHEC);
        }

        return toNotificationDto(notificationRepository.save(notification));
    }

    // ─── Méthode interne : accès depuis d'autres services ────────────────────────

    @Transactional(readOnly = true)
    public Client findClientById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    // ─── Helpers privés ──────────────────────────────────────────────────────────

    private void validerCanalClient(Client client, CanalNotification canal) {
        if (canal == CanalNotification.SMS && (client.getTelephone() == null || client.getTelephone().isBlank())) {
            throw new BusinessException("Ce client n'a pas de numéro de téléphone pour l'envoi SMS");
        }
        if (canal == CanalNotification.EMAIL && (client.getEmail() == null || client.getEmail().isBlank())) {
            throw new BusinessException("Ce client n'a pas d'adresse email pour l'envoi EMAIL");
        }
    }

    private String contactPour(Client client, CanalNotification canal) {
        return canal == CanalNotification.SMS ? client.getTelephone() : client.getEmail();
    }

    private ClientDto toDto(Client client) {
        return ClientDto.builder()
                .id(client.getId())
                .nom(client.getNom())
                .telephone(client.getTelephone())
                .email(client.getEmail())
                .adresse(client.getAdresse())
                .peutEtreRelance(client.peutEtreRelance())
                .createdAt(client.getCreatedAt())
                .build();
    }

    private NotificationDto toNotificationDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .clientId(n.getClient().getId())
                .nomClient(n.getClient().getNom())
                .canal(n.getCanal())
                .objet(n.getObjet())
                .contenu(n.getContenu())
                .statut(n.getStatut())
                .envoyeA(n.getEnvoyeA())
                .build();
    }
}
