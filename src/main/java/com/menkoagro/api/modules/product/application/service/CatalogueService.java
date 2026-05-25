package com.menkoagro.api.modules.product.application.service;

import com.menkoagro.api.modules.product.application.dto.CategorieProduitDto;
import com.menkoagro.api.modules.product.application.dto.CategorieProduitRequest;
import com.menkoagro.api.modules.product.application.dto.TypeCategorieDto;
import com.menkoagro.api.modules.product.application.dto.TypeCategorieRequest;
import com.menkoagro.api.modules.product.domain.entity.CategorieProduit;
import com.menkoagro.api.modules.product.domain.entity.TypeCategorie;
import com.menkoagro.api.modules.product.domain.repository.CategorieProduitRepository;
import com.menkoagro.api.modules.product.domain.repository.TypeCategorieRepository;
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
public class CatalogueService {

    private final TypeCategorieRepository typeCategorieRepository;
    private final CategorieProduitRepository categorieProduitRepository;

    // ─── TypeCategorie ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<TypeCategorieDto> getAllTypes() {
        return typeCategorieRepository.findAll().stream()
                .map(this::toTypeCategorieDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TypeCategorieDto createType(TypeCategorieRequest request) {
        String nom = request.getNom().toUpperCase();
        if (typeCategorieRepository.existsByNom(nom)) {
            throw new BusinessException("Un type de catégorie '" + nom + "' existe déjà");
        }
        TypeCategorie type = TypeCategorie.builder()
                .nom(nom)
                .description(request.getDescription())
                .build();
        return toTypeCategorieDto(typeCategorieRepository.save(type));
    }

    @Transactional
    public TypeCategorieDto updateType(UUID id, TypeCategorieRequest request) {
        TypeCategorie type = findTypeById(id);
        String nom = request.getNom().toUpperCase();
        if (typeCategorieRepository.existsByNomAndIdNot(nom, id)) {
            throw new BusinessException("Un type de catégorie '" + nom + "' existe déjà");
        }
        type.setNom(nom);
        type.setDescription(request.getDescription());
        return toTypeCategorieDto(typeCategorieRepository.save(type));
    }

    @Transactional
    public void deleteType(UUID id) {
        if (!typeCategorieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Type de catégorie", id);
        }
        if (categorieProduitRepository.existsByTypeCategorieId(id)) {
            throw new BusinessException("Impossible de supprimer ce type : des catégories y sont rattachées");
        }
        typeCategorieRepository.deleteById(id);
    }

    // ─── CategorieProduit ─────────────────────────────────────────────────────────

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

    @Transactional
    public CategorieProduitDto createCategorie(CategorieProduitRequest request) {
        TypeCategorie type = findTypeById(request.getTypeCategorieId());
        if (categorieProduitRepository.existsByNomAndTypeCategorieId(request.getNom(), request.getTypeCategorieId())) {
            throw new BusinessException("Une catégorie '" + request.getNom() + "' existe déjà dans ce type");
        }
        CategorieProduit categorie = CategorieProduit.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .typeCategorie(type)
                .build();
        return toCategorieDto(categorieProduitRepository.save(categorie));
    }

    @Transactional
    public CategorieProduitDto updateCategorie(UUID id, CategorieProduitRequest request) {
        CategorieProduit categorie = findCategorieById(id);
        TypeCategorie type = findTypeById(request.getTypeCategorieId());
        if (categorieProduitRepository.existsByNomAndTypeCategorieIdAndIdNot(
                request.getNom(), request.getTypeCategorieId(), id)) {
            throw new BusinessException("Une catégorie '" + request.getNom() + "' existe déjà dans ce type");
        }
        categorie.setNom(request.getNom());
        categorie.setDescription(request.getDescription());
        categorie.setTypeCategorie(type);
        return toCategorieDto(categorieProduitRepository.save(categorie));
    }

    @Transactional
    public void deleteCategorie(UUID id) {
        if (!categorieProduitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Catégorie", id);
        }
        categorieProduitRepository.deleteById(id);
    }

    // ─── Accès package pour ProduitService ───────────────────────────────────────

    TypeCategorie findTypeById(UUID id) {
        return typeCategorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Type de catégorie", id));
    }

    CategorieProduit findCategorieById(UUID id) {
        return categorieProduitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
    }

    // ─── Mappers ──────────────────────────────────────────────────────────────────

    private TypeCategorieDto toTypeCategorieDto(TypeCategorie t) {
        return TypeCategorieDto.builder()
                .id(t.getId())
                .nom(t.getNom())
                .description(t.getDescription())
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