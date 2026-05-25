package com.menkoagro.api.modules.production.application.service;

import com.menkoagro.api.modules.production.application.dto.CategorieCoutDto;
import com.menkoagro.api.modules.production.application.dto.CoutProductionDto;
import com.menkoagro.api.modules.production.application.dto.CoutProductionRequest;
import com.menkoagro.api.modules.production.domain.entity.CategorieCout;
import com.menkoagro.api.modules.production.domain.entity.CoutProduction;
import com.menkoagro.api.modules.production.domain.entity.Production;
import com.menkoagro.api.modules.production.domain.repository.CoutProductionRepository;
import com.menkoagro.api.modules.production.domain.repository.ProductionRepository;
import com.menkoagro.api.shared.exception.BusinessException;
import com.menkoagro.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoutProductionService {

    private final CoutProductionRepository coutProductionRepository;
    private final ProductionRepository productionRepository;

    public List<CategorieCoutDto> getCategoriesCout() {
        return Arrays.stream(CategorieCout.values())
                .map(c -> CategorieCoutDto.builder()
                        .code(c.name())
                        .libelle(toLibelle(c))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CoutProductionDto> getCouts(UUID productionId) {
        findProductionById(productionId);
        return coutProductionRepository.findByProductionId(productionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

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

        return toDto(coutProductionRepository.save(cout));
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

    private CoutProductionDto toDto(CoutProduction c) {
        return CoutProductionDto.builder()
                .id(c.getId())
                .categorie(c.getCategorie())
                .libelle(c.getLibelle())
                .montant(c.getMontant())
                .date(c.getDate())
                .build();
    }

    private String toLibelle(CategorieCout categorie) {
        return switch (categorie) {
            case INTRANTS -> "Intrants agricoles";
            case MAIN_OEUVRE -> "Main d'œuvre";
            case TRANSPORT -> "Transport";
            case VETERINAIRE -> "Vétérinaire";
            case ALIMENTATION -> "Alimentation animale";
            case AUTRE -> "Autre";
        };
    }
}