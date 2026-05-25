package com.menkoagro.api.modules.production.application.service;

import com.menkoagro.api.modules.production.application.dto.EtapeProductionDto;
import com.menkoagro.api.modules.production.application.dto.EtapeProductionRequest;
import com.menkoagro.api.modules.production.domain.entity.EtapeProduction;
import com.menkoagro.api.modules.production.domain.entity.Production;
import com.menkoagro.api.modules.production.domain.entity.ProductionAgricole;
import com.menkoagro.api.modules.production.domain.repository.EtapeProductionRepository;
import com.menkoagro.api.modules.production.domain.repository.ProductionRepository;
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
public class EtapeProductionService {

    private final EtapeProductionRepository etapeProductionRepository;
    private final ProductionRepository productionRepository;

    @Transactional(readOnly = true)
    public List<EtapeProductionDto> getEtapes(UUID productionId) {
        findAgricoleById(productionId);
        return etapeProductionRepository.findByProductionId(productionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EtapeProductionDto addEtape(UUID productionId, EtapeProductionRequest request) {
        ProductionAgricole agricole = findAgricoleById(productionId);
        if (!agricole.estEnCours()) {
            throw new BusinessException("Impossible d'ajouter une étape : production non en cours");
        }

        EtapeProduction etape = EtapeProduction.builder()
                .production(agricole)
                .type(request.getType())
                .dateRealisation(request.getDateRealisation())
                .notes(request.getNotes())
                .build();

        return toDto(etapeProductionRepository.save(etape));
    }

    @Transactional
    public EtapeProductionDto updateEtape(UUID productionId, UUID etapeId, EtapeProductionRequest request) {
        findAgricoleById(productionId);
        EtapeProduction etape = etapeProductionRepository.findById(etapeId)
                .orElseThrow(() -> new ResourceNotFoundException("Étape", etapeId));
        if (!etape.getProduction().getId().equals(productionId)) {
            throw new BusinessException("Cette étape n'appartient pas à cette production");
        }

        etape.setType(request.getType());
        etape.setDateRealisation(request.getDateRealisation());
        etape.setNotes(request.getNotes());

        return toDto(etapeProductionRepository.save(etape));
    }

    @Transactional
    public void deleteEtape(UUID productionId, UUID etapeId) {
        findAgricoleById(productionId);
        EtapeProduction etape = etapeProductionRepository.findById(etapeId)
                .orElseThrow(() -> new ResourceNotFoundException("Étape", etapeId));
        if (!etape.getProduction().getId().equals(productionId)) {
            throw new BusinessException("Cette étape n'appartient pas à cette production");
        }
        etapeProductionRepository.deleteById(etapeId);
    }

    // ─── Helpers privés ──────────────────────────────────────────────────────────

    private ProductionAgricole findAgricoleById(UUID productionId) {
        Production production = productionRepository.findById(productionId)
                .orElseThrow(() -> new ResourceNotFoundException("Production", productionId));
        if (!(production instanceof ProductionAgricole agricole)) {
            throw new BusinessException("Les étapes ne s'appliquent qu'aux productions agricoles");
        }
        return agricole;
    }

    private EtapeProductionDto toDto(EtapeProduction e) {
        return EtapeProductionDto.builder()
                .id(e.getId())
                .type(e.getType())
                .dateRealisation(e.getDateRealisation())
                .notes(e.getNotes())
                .build();
    }
}