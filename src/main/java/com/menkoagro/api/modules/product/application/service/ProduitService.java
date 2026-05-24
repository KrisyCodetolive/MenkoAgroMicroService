package com.menkoagro.api.modules.product.application.service;

import com.menkoagro.api.modules.product.application.dto.CategorieProduitDto;
import com.menkoagro.api.modules.product.application.dto.ConditionnementDto;
import com.menkoagro.api.modules.product.application.dto.ConditionnementRequest;
import com.menkoagro.api.modules.product.application.dto.ProduitDto;
import com.menkoagro.api.modules.product.application.dto.ProduitRequest;
import com.menkoagro.api.modules.product.application.dto.TypeCategorieDto;
import com.menkoagro.api.modules.product.domain.entity.CategorieProduit;
import com.menkoagro.api.modules.product.domain.entity.Conditionnement;
import com.menkoagro.api.modules.product.domain.entity.Produit;
import com.menkoagro.api.modules.product.domain.entity.TypeCategorie;
import com.menkoagro.api.modules.product.domain.repository.CategorieProduitRepository;
import com.menkoagro.api.modules.product.domain.repository.ConditionnementRepository;
import com.menkoagro.api.modules.product.domain.repository.ProduitRepository;
import com.menkoagro.api.modules.product.domain.repository.TypeCategorieRepository;
import com.menkoagro.api.modules.stock.application.service.StockService;
import com.menkoagro.api.shared.exception.BusinessException;
import com.menkoagro.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieProduitRepository categorieProduitRepository;
    private final TypeCategorieRepository typeCategorieRepository;
    private final ConditionnementRepository conditionnementRepository;
    private final StockService stockService;

    // ─── Produits ────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ProduitDto> getAll() {
        return produitRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProduitDto> getByCategorie(UUID categorieId) {
        return produitRepository.findByCategorieId(categorieId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProduitDto getById(UUID id) {
        return toDto(findProduitById(id));
    }

    @Transactional
    public ProduitDto create(ProduitRequest request) {
        CategorieProduit categorie = findCategorieById(request.getCategorieId());

        if (produitRepository.existsByNomAndCategorieId(request.getNom(), request.getCategorieId())) {
            throw new BusinessException("Un produit '" + request.getNom() + "' existe déjà dans cette catégorie");
        }

        Produit produit = Produit.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .uniteBase(request.getUniteBase())
                .categorie(categorie)
                .build();

        // Ajout des conditionnements si fournis
        if (request.getConditionnements() != null && !request.getConditionnements().isEmpty()) {
            boolean hasDefault = request.getConditionnements().stream()
                    .anyMatch(ConditionnementRequest::isEstParDefaut);

            for (int i = 0; i < request.getConditionnements().size(); i++) {
                ConditionnementRequest cr = request.getConditionnements().get(i);
                Conditionnement cond = buildConditionnement(cr, produit);
                // Premier conditionnement devient par défaut si aucun n'est spécifié
                if (!hasDefault && i == 0) {
                    cond.setEstParDefaut(true);
                }
                produit.getConditionnements().add(cond);
            }
        }

        Produit saved = produitRepository.save(produit);

        // Création automatique du stock initial à zéro
        stockService.creerStockInitial(saved);

        return toDto(saved);
    }

    @Transactional
    public ProduitDto update(UUID id, ProduitRequest request) {
        Produit produit = findProduitById(id);
        CategorieProduit categorie = findCategorieById(request.getCategorieId());

        // Vérifie l'unicité si nom ou catégorie changé
        if (!produit.getNom().equals(request.getNom()) || !produit.getCategorie().getId().equals(request.getCategorieId())) {
            if (produitRepository.existsByNomAndCategorieId(request.getNom(), request.getCategorieId())) {
                throw new BusinessException("Un produit '" + request.getNom() + "' existe déjà dans cette catégorie");
            }
        }

        produit.setNom(request.getNom());
        produit.setDescription(request.getDescription());
        produit.setUniteBase(request.getUniteBase());
        produit.setCategorie(categorie);

        return toDto(produitRepository.save(produit));
    }

    @Transactional
    public void delete(UUID id) {
        if (!produitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit", id);
        }
        produitRepository.deleteById(id);
    }

    // ─── Conditionnements ────────────────────────────────────────────────────────

    @Transactional
    public ConditionnementDto addConditionnement(UUID produitId, ConditionnementRequest request) {
        Produit produit = findProduitById(produitId);

        // Si c'est le premier conditionnement, le définir par défaut automatiquement
        boolean premierCond = produit.getConditionnements().isEmpty();

        Conditionnement cond = buildConditionnement(request, produit);
        if (premierCond) {
            cond.setEstParDefaut(true);
        }

        // Si marqué comme défaut, désactiver les autres
        if (request.isEstParDefaut()) {
            produit.getConditionnements().forEach(c -> c.setEstParDefaut(false));
            cond.setEstParDefaut(true);
        }

        produit.getConditionnements().add(cond);
        Produit saved = produitRepository.save(produit);

        return toConditionnementDto(
                saved.getConditionnements().get(saved.getConditionnements().size() - 1)
        );
    }

    @Transactional
    public ConditionnementDto updateConditionnement(UUID produitId, UUID conditionnementId,
                                                    ConditionnementRequest request) {
        Produit produit = findProduitById(produitId);
        Conditionnement cond = findConditionnementDuProduit(produit, conditionnementId);

        cond.setLibelle(request.getLibelle());
        cond.setQuantiteBase(request.getQuantiteBase());
        cond.setPrixVente(request.getPrixVente());

        if (request.isEstParDefaut()) {
            produit.getConditionnements().forEach(c -> c.setEstParDefaut(false));
            cond.setEstParDefaut(true);
        }

        produitRepository.save(produit);
        return toConditionnementDto(cond);
    }

    @Transactional
    public void deleteConditionnement(UUID produitId, UUID conditionnementId) {
        Produit produit = findProduitById(produitId);
        Conditionnement cond = findConditionnementDuProduit(produit, conditionnementId);

        if (cond.isEstParDefaut() && produit.getConditionnements().size() > 1) {
            throw new BusinessException("Impossible de supprimer le conditionnement par défaut. Définissez-en un autre d'abord.");
        }

        produit.getConditionnements().remove(cond);
        produitRepository.save(produit);
    }

    @Transactional
    public ConditionnementDto setConditionnementParDefaut(UUID produitId, UUID conditionnementId) {
        Produit produit = findProduitById(produitId);
        Conditionnement cond = findConditionnementDuProduit(produit, conditionnementId);

        produit.getConditionnements().forEach(c -> c.setEstParDefaut(false));
        cond.setEstParDefaut(true);

        produitRepository.save(produit);
        return toConditionnementDto(cond);
    }

    // ─── Catalogue ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TypeCategorieDto> getAllTypes() {
        return typeCategorieRepository.findAll().stream()
                .map(t -> TypeCategorieDto.builder()
                        .id(t.getId())
                        .nom(t.getNom())
                        .description(t.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategorieProduitDto> getAllCategories() {
        return categorieProduitRepository.findAll().stream()
                .map(this::toCategorieDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategorieProduitDto> getCategoriesByType(UUID typeCategorieId) {
        findTypeById(typeCategorieId);
        return categorieProduitRepository.findByTypeCategorieId(typeCategorieId).stream()
                .map(this::toCategorieDto)
                .collect(Collectors.toList());
    }

    // ─── Helpers privés ──────────────────────────────────────────────────────────

    private Produit findProduitById(UUID id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
    }

    private CategorieProduit findCategorieById(UUID id) {
        return categorieProduitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
    }

    private TypeCategorie findTypeById(UUID id) {
        return typeCategorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de catégorie", id));
    }

    private Conditionnement findConditionnementDuProduit(Produit produit, UUID conditionnementId) {
        return produit.getConditionnements().stream()
                .filter(c -> c.getId().equals(conditionnementId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Conditionnement", conditionnementId));
    }

    private Conditionnement buildConditionnement(ConditionnementRequest request, Produit produit) {
        return Conditionnement.builder()
                .produit(produit)
                .libelle(request.getLibelle())
                .quantiteBase(request.getQuantiteBase())
                .prixVente(request.getPrixVente())
                .estParDefaut(request.isEstParDefaut())
                .build();
    }

    public ProduitDto toDto(Produit produit) {
        List<ConditionnementDto> conditionnements = produit.getConditionnements().stream()
                .map(this::toConditionnementDto)
                .collect(Collectors.toList());

        return ProduitDto.builder()
                .id(produit.getId())
                .nom(produit.getNom())
                .description(produit.getDescription())
                .uniteBase(produit.getUniteBase())
                .categorieId(produit.getCategorie().getId())
                .nomCategorie(produit.getCategorie().getNom())
                .typeCategorie(produit.getCategorie().getTypeCategorie().getNom())
                .conditionnements(conditionnements)
                .createdAt(produit.getCreatedAt())
                .updatedAt(produit.getUpdatedAt())
                .build();
    }

    private ConditionnementDto toConditionnementDto(Conditionnement c) {
        return ConditionnementDto.builder()
                .id(c.getId())
                .libelle(c.getLibelle())
                .quantiteBase(c.getQuantiteBase())
                .prixVente(c.getPrixVente())
                .estParDefaut(c.isEstParDefaut())
                .build();
    }

    private CategorieProduitDto toCategorieDto(CategorieProduit c) {
        return CategorieProduitDto.builder()
                .id(c.getId())
                .nom(c.getNom())
                .description(c.getDescription())
                .typeCategorieId(c.getTypeCategorie().getId())
                .nomTypeCategorie(c.getTypeCategorie().getNom())
                .build();
    }
}
