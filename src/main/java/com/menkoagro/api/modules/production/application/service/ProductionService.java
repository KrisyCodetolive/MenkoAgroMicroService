package com.menkoagro.api.modules.production.application.service;

import com.menkoagro.api.modules.product.domain.entity.Produit;
import com.menkoagro.api.modules.product.domain.repository.ProduitRepository;
import com.menkoagro.api.modules.production.application.dto.CloturerProductionRequest;
import com.menkoagro.api.modules.production.application.dto.CoutProductionDto;
import com.menkoagro.api.modules.production.application.dto.CoutProductionRequest;
import com.menkoagro.api.modules.production.application.dto.ElevageBandeRequest;
import com.menkoagro.api.modules.production.application.dto.EtapeProductionDto;
import com.menkoagro.api.modules.production.application.dto.ProductionAgricoleRequest;
import com.menkoagro.api.modules.production.application.dto.ProductionDto;
import com.menkoagro.api.modules.production.domain.entity.CoutProduction;
import com.menkoagro.api.modules.production.domain.entity.ElevageBande;
import com.menkoagro.api.modules.production.domain.entity.EtapeProduction;
import com.menkoagro.api.modules.production.domain.entity.Production;
import com.menkoagro.api.modules.production.domain.entity.ProductionAgricole;
import com.menkoagro.api.modules.production.domain.entity.StatutProduction;
import com.menkoagro.api.modules.production.domain.repository.CoutProductionRepository;
import com.menkoagro.api.modules.production.domain.repository.ProductionRepository;
import com.menkoagro.api.modules.stock.application.service.StockService;
import com.menkoagro.api.modules.stock.domain.entity.MotifMouvement;
import com.menkoagro.api.modules.stock.domain.entity.TypeMouvement;
import com.menkoagro.api.shared.exception.BusinessException;
import com.menkoagro.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final CoutProductionRepository coutProductionRepository;
    private final ProduitRepository produitRepository;
    private final StockService stockService;

    // ─── Lecture ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ProductionDto> getAll() {
        return productionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductionDto> getByStatut(StatutProduction statut) {
        return productionRepository.findByStatut(statut).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductionDto> getByProduit(UUID produitId) {
        return productionRepository.findByProduitId(produitId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductionDto getById(UUID id) {
        return toDto(findProductionById(id));
    }

    // ─── Création ────────────────────────────────────────────────────────────────

    @Transactional
    public ProductionDto creerAgricole(ProductionAgricoleRequest request) {
        Produit produit = findProduitById(request.getProduitId());

        ProductionAgricole production = new ProductionAgricole();
        production.setProduit(produit);
        production.setDateDebut(request.getDateDebut());
        production.setStatut(StatutProduction.EN_COURS);
        production.setZone(request.getZone());
        production.setSuperficieParcelle(request.getSuperficieParcelle());

        return toDto(productionRepository.save(production));
    }

    @Transactional
    public ProductionDto creerElevage(ElevageBandeRequest request) {
        Produit produit = findProduitById(request.getProduitId());

        ElevageBande production = new ElevageBande();
        production.setProduit(produit);
        production.setDateDebut(request.getDateDebut());
        production.setStatut(StatutProduction.EN_COURS);
        production.setReference(request.getReference());
        production.setNombreAnimaux(request.getNombreAnimaux());
        production.setTypeAnimal(request.getTypeAnimal());

        return toDto(productionRepository.save(production));
    }

    // ─── Clôture / Abandon ───────────────────────────────────────────────────────

    @Transactional
    public ProductionDto cloturer(UUID id, CloturerProductionRequest request) {
        Production production = findProductionById(id);

        if (!production.estEnCours()) {
            throw new BusinessException("Cette production est déjà " + production.getStatut(),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        production.setStatut(StatutProduction.TERMINEE);
        production.setDateFin(request.getDateFin() != null ? request.getDateFin() : LocalDate.now());

        if (production instanceof ElevageBande elevage && request.getTauxMortalite() != null) {
            elevage.setTauxMortalite(request.getTauxMortalite());
        }

        Production saved = productionRepository.save(production);

        // Alimentation automatique du stock
        stockService.appliquerMouvement(
                production.getProduit().getId(),
                TypeMouvement.ENTREE,
                request.getQuantiteProduite(),
                MotifMouvement.PRODUCTION,
                "PROD-" + id.toString().substring(0, 8).toUpperCase()
        );

        return toDto(saved);
    }

    @Transactional
    public ProductionDto abandonner(UUID id) {
        Production production = findProductionById(id);

        if (!production.estEnCours()) {
            throw new BusinessException("Cette production est déjà " + production.getStatut(),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        production.setStatut(StatutProduction.ABANDONNEE);
        production.setDateFin(LocalDate.now());

        return toDto(productionRepository.save(production));
    }

    // ─── Coûts ───────────────────────────────────────────────────────────────────

    @Transactional
    public CoutProductionDto addCout(UUID productionId, CoutProductionRequest request) {
        Production production = findProductionById(productionId);

        CoutProduction cout = CoutProduction.builder()
                .production(production)
                .categorie(request.getCategorie())
                .libelle(request.getLibelle())
                .montant(request.getMontant())
                .date(request.getDate())
                .build();

        // Sauvegarde directe pour récupérer l'UUID généré
        CoutProduction saved = coutProductionRepository.save(cout);
        return toCoutDto(saved);
    }

    @Transactional
    public void deleteCout(UUID productionId, UUID coutId) {
        findProductionById(productionId);
        CoutProduction cout = coutProductionRepository.findById(coutId)
                .orElseThrow(() -> new ResourceNotFoundException("Coût", coutId));
        if (!cout.getProduction().getId().equals(productionId)) {
            throw new BusinessException("Ce coût n'appartient pas à cette production");
        }
        coutProductionRepository.deleteById(coutId);
    }

    // ─── Helpers privés ──────────────────────────────────────────────────────────

    private Production findProductionById(UUID id) {
        return productionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Production", id));
    }

    private Produit findProduitById(UUID id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
    }

    public ProductionDto toDto(Production production) {
        ProductionDto.ProductionDtoBuilder builder = ProductionDto.builder()
                .id(production.getId())
                .produitId(production.getProduit().getId())
                .nomProduit(production.getProduit().getNom())
                .dateDebut(production.getDateDebut())
                .dateFin(production.getDateFin())
                .statut(production.getStatut())
                .coutTotal(production.calculerCoutTotal())
                .couts(production.getCouts().stream().map(this::toCoutDto).collect(Collectors.toList()))
                .createdAt(production.getCreatedAt());

        // Pattern matching Java 21 pour les sous-types
        if (production instanceof ProductionAgricole agricole) {
            builder.type("AGRICOLE")
                    .zone(agricole.getZone())
                    .superficieParcelle(agricole.getSuperficieParcelle())
                    .etapes(agricole.getEtapes().stream().map(this::toEtapeDto).collect(Collectors.toList()));
        } else if (production instanceof ElevageBande elevage) {
            builder.type("ELEVAGE")
                    .reference(elevage.getReference())
                    .nombreAnimaux(elevage.getNombreAnimaux())
                    .typeAnimal(elevage.getTypeAnimal())
                    .tauxMortalite(elevage.getTauxMortalite());
        }

        return builder.build();
    }

    private EtapeProductionDto toEtapeDto(EtapeProduction e) {
        return EtapeProductionDto.builder()
                .id(e.getId())
                .type(e.getType())
                .dateRealisation(e.getDateRealisation())
                .notes(e.getNotes())
                .build();
    }

    private CoutProductionDto toCoutDto(CoutProduction c) {
        return CoutProductionDto.builder()
                .id(c.getId())
                .categorie(c.getCategorie())
                .libelle(c.getLibelle())
                .montant(c.getMontant())
                .date(c.getDate())
                .build();
    }
}
