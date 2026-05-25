package com.menkoagro.api.modules.sales.application.service;

import com.menkoagro.api.modules.customer.application.service.ClientService;
import com.menkoagro.api.modules.customer.domain.entity.Client;
import com.menkoagro.api.modules.product.domain.entity.Conditionnement;
import com.menkoagro.api.modules.product.domain.entity.Produit;
import com.menkoagro.api.modules.product.domain.repository.ConditionnementRepository;
import com.menkoagro.api.modules.product.domain.repository.ProduitRepository;
import com.menkoagro.api.modules.sales.application.dto.LigneVenteDto;
import com.menkoagro.api.modules.sales.application.dto.LigneVenteRequest;
import com.menkoagro.api.modules.sales.application.dto.RecuDto;
import com.menkoagro.api.modules.sales.application.dto.VenteDto;
import com.menkoagro.api.modules.sales.application.dto.VenteRequest;
import com.menkoagro.api.modules.sales.domain.entity.LigneVente;
import com.menkoagro.api.modules.sales.domain.entity.Recu;
import com.menkoagro.api.modules.sales.domain.entity.Vente;
import com.menkoagro.api.modules.sales.domain.repository.VenteRepository;
import com.menkoagro.api.modules.stock.application.service.StockService;
import com.menkoagro.api.modules.stock.domain.entity.MotifMouvement;
import com.menkoagro.api.modules.stock.domain.entity.TypeMouvement;
import com.menkoagro.api.shared.exception.BusinessException;
import com.menkoagro.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository venteRepository;
    private final ClientService clientService;
    private final ProduitRepository produitRepository;
    private final ConditionnementRepository conditionnementRepository;
    private final StockService stockService;
    private final PdfRecuService pdfRecuService;

    // ─── Lecture ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<VenteDto> getAll() {
        return venteRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VenteDto> getByClient(UUID clientId) {
        clientService.findClientById(clientId);
        return venteRepository.findByClientIdOrderByDateVenteDesc(clientId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VenteDto getById(UUID id) {
        return toDto(findVenteById(id));
    }

    // ─── Création ────────────────────────────────────────────────────────────────

    @Transactional
    public VenteDto creerVente(VenteRequest request) {
        Client client = clientService.findClientById(request.getClientId());

        // 1. Valider et construire les lignes
        List<LigneVente> lignesValidees = validerEtBuildLignes(request.getLignes());

        // 2. Construire la vente
        Vente vente = Vente.builder()
                .client(client)
                .build();

        BigDecimal montantTotal = BigDecimal.ZERO;
        for (LigneVente ligne : lignesValidees) {
            ligne.setVente(vente);
            vente.getLignes().add(ligne);
            montantTotal = montantTotal.add(ligne.getSousTotal());
        }
        vente.setMontantTotal(montantTotal);

        // 3. Sauvegarder (cascade sur les lignes)
        Vente saved = venteRepository.save(vente);

        // 4. Décrémenter le stock pour chaque ligne
        for (LigneVente ligne : saved.getLignes()) {
            stockService.appliquerMouvement(
                    ligne.getProduit().getId(),
                    TypeMouvement.SORTIE,
                    ligne.getQteDeduitStock(),
                    MotifMouvement.VENTE,
                    "VENTE-" + saved.getId().toString().substring(0, 8).toUpperCase()
            );
        }

        // 5. Générer le reçu PDF
        Recu recu = pdfRecuService.genererRecu(saved);
        saved.setRecu(recu);
        venteRepository.save(saved);

        return toDto(saved);
    }

    // ─── Téléchargement PDF ───────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public byte[] getRecuPdf(UUID venteId) {
        Vente vente = findVenteById(venteId);
        if (vente.getRecu() == null) {
            throw new ResourceNotFoundException("Reçu PDF introuvable pour la vente : " + venteId);
        }
        try {
            return Files.readAllBytes(Paths.get(vente.getRecu().getCheminFichier()));
        } catch (Exception e) {
            throw new BusinessException("Impossible de lire le fichier PDF : " + e.getMessage());
        }
    }

    // ─── Helpers privés ──────────────────────────────────────────────────────────

    private List<LigneVente> validerEtBuildLignes(List<LigneVenteRequest> requests) {
        List<LigneVente> lignes = new ArrayList<>();

        for (LigneVenteRequest lr : requests) {
            Produit produit = produitRepository.findById(lr.getProduitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produit", lr.getProduitId()));

            Conditionnement cond = conditionnementRepository.findById(lr.getConditionnementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conditionnement", lr.getConditionnementId()));

            // Vérification : le conditionnement appartient bien au produit
            if (!cond.getProduit().getId().equals(produit.getId())) {
                throw new BusinessException(
                        "Le conditionnement '" + cond.getLibelle() + "' n'appartient pas au produit '" + produit.getNom() + "'");
            }

            // qteDeduitStock = quantite_commandée × quantiteBase du conditionnement
            BigDecimal qteDeduit = lr.getQuantite()
                    .multiply(cond.getQuantiteBase());
            BigDecimal sousTotal = lr.getQuantite().multiply(cond.getPrixVente());

            lignes.add(LigneVente.builder()
                    .produit(produit)
                    .conditionnement(cond)
                    .quantite(lr.getQuantite())
                    .prixUnitaire(cond.getPrixVente())
                    .sousTotal(sousTotal)
                    .qteDeduitStock(qteDeduit)
                    .build());
        }
        return lignes;
    }

    private Vente findVenteById(UUID id) {
        return venteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vente", id));
    }

    private VenteDto toDto(Vente vente) {
        List<LigneVenteDto> lignesDto = vente.getLignes().stream()
                .map(l -> LigneVenteDto.builder()
                        .id(l.getId())
                        .produitId(l.getProduit().getId())
                        .nomProduit(l.getProduit().getNom())
                        .conditionnementId(l.getConditionnement().getId())
                        .libelleConditionnement(l.getConditionnement().getLibelle())
                        .quantite(l.getQuantite())
                        .prixUnitaire(l.getPrixUnitaire())
                        .sousTotal(l.getSousTotal())
                        .qteDeduitStock(l.getQteDeduitStock())
                        .build())
                .collect(Collectors.toList());

        RecuDto recuDto = null;
        if (vente.getRecu() != null) {
            recuDto = RecuDto.builder()
                    .id(vente.getRecu().getId())
                    .numeroRecu(vente.getRecu().getNumeroRecu())
                    .cheminFichier(vente.getRecu().getCheminFichier())
                    .genereA(vente.getRecu().getGenereA())
                    .build();
        }

        return VenteDto.builder()
                .id(vente.getId())
                .clientId(vente.getClient().getId())
                .nomClient(vente.getClient().getNom())
                .montantTotal(vente.getMontantTotal())
                .dateVente(vente.getDateVente())
                .lignes(lignesDto)
                .recu(recuDto)
                .build();
    }
}
