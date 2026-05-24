package com.menkoagro.api.modules.rapport.application.service;

import com.menkoagro.api.modules.production.domain.entity.Production;
import com.menkoagro.api.modules.production.domain.entity.StatutProduction;
import com.menkoagro.api.modules.production.domain.repository.ProductionRepository;
import com.menkoagro.api.modules.rapport.application.dto.RentabiliteDto;
import com.menkoagro.api.modules.rapport.application.dto.RentabiliteParProduitDto;
import com.menkoagro.api.modules.sales.domain.entity.LigneVente;
import com.menkoagro.api.modules.sales.domain.entity.Vente;
import com.menkoagro.api.modules.sales.domain.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RapportService {

    private final VenteRepository venteRepository;
    private final ProductionRepository productionRepository;

    public RentabiliteDto calculerRentabilite(LocalDate dateDebut, LocalDate dateFin) {
        LocalDateTime debutDt = dateDebut.atStartOfDay();
        LocalDateTime finDt = dateFin.atTime(LocalTime.MAX);

        // Aggregate vente revenue per produit
        Map<UUID, BigDecimal> revenus = new HashMap<>();
        Map<UUID, String> noms = new HashMap<>();
        // Compter les ventes distinctes (pas les lignes) contenant ce produit
        Map<UUID, java.util.Set<UUID>> venteIdsParProduit = new HashMap<>();

        List<Vente> ventes = venteRepository.findByDateVenteBetween(debutDt, finDt);
        for (Vente vente : ventes) {
            for (LigneVente ligne : vente.getLignes()) {
                UUID pid = ligne.getProduit().getId();
                noms.put(pid, ligne.getProduit().getNom());
                revenus.merge(pid, ligne.getSousTotal(), BigDecimal::add);
                venteIdsParProduit.computeIfAbsent(pid, k -> new java.util.HashSet<>()).add(vente.getId());
            }
        }

        // Aggregate production cost per produit (TERMINEE only)
        Map<UUID, BigDecimal> couts = new HashMap<>();
        Map<UUID, Long> nbProds = new HashMap<>();

        List<Production> productions = productionRepository.findByStatutAndDateFinBetween(
                StatutProduction.TERMINEE, dateDebut, dateFin);
        for (Production prod : productions) {
            UUID pid = prod.getProduit().getId();
            noms.put(pid, prod.getProduit().getNom());
            couts.merge(pid, prod.calculerCoutTotal(), BigDecimal::add);
            nbProds.merge(pid, 1L, Long::sum);
        }

        // Build per-product detail
        Map<UUID, RentabiliteParProduitDto> detailMap = new HashMap<>();
        for (UUID pid : noms.keySet()) {
            BigDecimal rev = revenus.getOrDefault(pid, BigDecimal.ZERO);
            BigDecimal cout = couts.getOrDefault(pid, BigDecimal.ZERO);
            BigDecimal marge = rev.subtract(cout);
            BigDecimal taux = rev.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : marge.divide(rev, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            long nbVentesDistinctes = venteIdsParProduit.getOrDefault(pid, java.util.Collections.emptySet()).size();
            detailMap.put(pid, RentabiliteParProduitDto.builder()
                    .produitId(pid)
                    .nomProduit(noms.get(pid))
                    .totalVentes(rev)
                    .totalCouts(cout)
                    .marge(marge)
                    .tauxMarge(taux)
                    .nombreVentes(nbVentesDistinctes)
                    .nombreProductions(nbProds.getOrDefault(pid, 0L))
                    .build());
        }

        List<RentabiliteParProduitDto> detail = new ArrayList<>(detailMap.values());
        detail.sort((a, b) -> b.getMarge().compareTo(a.getMarge()));

        BigDecimal totalRevenus = detail.stream().map(RentabiliteParProduitDto::getTotalVentes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCouts = detail.stream().map(RentabiliteParProduitDto::getTotalCouts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal margeNette = totalRevenus.subtract(totalCouts);
        BigDecimal tauxGlobal = totalRevenus.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : margeNette.divide(totalRevenus, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        return RentabiliteDto.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .totalRevenus(totalRevenus)
                .totalCouts(totalCouts)
                .margeNette(margeNette)
                .tauxMargePercent(tauxGlobal)
                .detail(detail)
                .build();
    }
}
