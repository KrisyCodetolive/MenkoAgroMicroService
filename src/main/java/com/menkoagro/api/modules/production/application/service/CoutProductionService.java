package com.menkoagro.api.modules.production.application.service;

import com.menkoagro.api.modules.production.application.dto.CategorieCoutDto;
import com.menkoagro.api.modules.production.application.dto.CoutProductionDto;
import com.menkoagro.api.modules.production.application.dto.CoutProductionRequest;
import com.menkoagro.api.modules.production.domain.entity.CategorieCout;
import com.menkoagro.api.modules.production.domain.entity.CoutProduction;
import com.menkoagro.api.modules.production.domain.entity.EtapeProduction;
import com.menkoagro.api.modules.production.domain.entity.Production;
import com.menkoagro.api.modules.production.domain.entity.ProductionAgricole;
import com.menkoagro.api.modules.production.domain.repository.CoutProductionRepository;
import com.menkoagro.api.modules.production.domain.repository.EtapeProductionRepository;
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
    private final EtapeProductionRepository etapeProductionRepository;

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

        EtapeProduction etape = null;
        if (request.getEtapeId() != null) {
            if (!(production instanceof ProductionAgricole)) {
                throw new BusinessException("Les coûts par étape ne s'appliquent qu'aux productions agricoles");
            }
            etape = etapeProductionRepository.findById(request.getEtapeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Étape", request.getEtapeId()));
            if (!etape.getProduction().getId().equals(productionId)) {
                throw new BusinessException("Cette étape n'appartient pas à cette production");
            }
        }

        CoutProduction cout = CoutProduction.builder()
                .production(production)
                .etape(etape)
                .categorie(request.getCategorie())
                .libelle(request.getLibelle())
                .montant(request.getMontant())
                .date(request.getDate())
                .build();

        return toDto(coutProductionRepository.save(cout));
    }

    @Transactional
    public CoutProductionDto addCoutParEtape(UUID productionId, UUID etapeId, CoutProductionRequest request) {
        Production production = findProductionById(productionId);
        if (!(production instanceof ProductionAgricole)) {
            throw new BusinessException("Les coûts par étape ne s'appliquent qu'aux productions agricoles");
        }
        EtapeProduction etape = etapeProductionRepository.findById(etapeId)
                .orElseThrow(() -> new ResourceNotFoundException("Étape", etapeId));
        if (!etape.getProduction().getId().equals(productionId)) {
            throw new BusinessException("Cette étape n'appartient pas à cette production");
        }

        CoutProduction cout = CoutProduction.builder()
                .production(production)
                .etape(etape)
                .categorie(request.getCategorie())
                .libelle(request.getLibelle())
                .montant(request.getMontant())
                .date(request.getDate())
                .build();

        return toDto(coutProductionRepository.save(cout));
    }

    @Transactional(readOnly = true)
    public List<CoutProductionDto> getCoutsParEtape(UUID productionId, UUID etapeId) {
        findProductionById(productionId);
        etapeProductionRepository.findById(etapeId)
                .orElseThrow(() -> new ResourceNotFoundException("Étape", etapeId));
        return coutProductionRepository.findByEtapeId(etapeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
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
                .etapeId(c.getEtape() != null ? c.getEtape().getId() : null)
                .typeEtape(c.getEtape() != null ? c.getEtape().getType() : null)
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