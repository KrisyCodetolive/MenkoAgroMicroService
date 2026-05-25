package com.menkoagro.api.modules.stock.application.service;

import com.menkoagro.api.modules.product.domain.entity.Produit;
import com.menkoagro.api.modules.stock.application.dto.AjustementStockRequest;
import com.menkoagro.api.modules.stock.application.dto.AlertePeremptionDto;
import com.menkoagro.api.modules.stock.application.dto.MouvementStockDto;
import com.menkoagro.api.modules.stock.application.dto.StockDto;
import com.menkoagro.api.modules.stock.domain.entity.MotifMouvement;
import com.menkoagro.api.modules.stock.domain.entity.MouvementStock;
import com.menkoagro.api.modules.stock.domain.entity.Stock;
import com.menkoagro.api.modules.stock.domain.entity.TypeMouvement;
import com.menkoagro.api.modules.stock.domain.repository.MouvementStockRepository;
import com.menkoagro.api.modules.stock.domain.repository.StockRepository;
import com.menkoagro.api.shared.exception.BusinessException;
import com.menkoagro.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final MouvementStockRepository mouvementStockRepository;

    @Transactional(readOnly = true)
    public List<StockDto> getAll() {
        return stockRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StockDto getById(UUID id) {
        return toDto(findStockById(id));
    }

    @Transactional(readOnly = true)
    public StockDto getByProduitId(UUID produitId) {
        Stock stock = stockRepository.findByProduitId(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable pour le produit : " + produitId));
        return toDto(stock);
    }

    @Transactional(readOnly = true)
    public List<StockDto> getStocksEnAlerte() {
        return stockRepository.findStocksEnAlerte().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertePeremptionDto> getAlertesPeremption() {
        return stockRepository.findStocksPerissables().stream()
                .map(stock -> {
                    Optional<MouvementStock> dernierEntree = mouvementStockRepository
                            .findFirstByStockIdAndTypeOrderByDateDesc(stock.getId(), TypeMouvement.ENTREE);

                    LocalDate dernierEntreeDate = dernierEntree
                            .map(m -> m.getDate().toLocalDate())
                            .orElse(stock.getUpdatedAt().toLocalDate());

                    LocalDate dateExpiration = dernierEntreeDate
                            .plusDays(stock.getProduit().getDureeConservationJours());
                    long joursRestants = ChronoUnit.DAYS.between(LocalDate.now(), dateExpiration);

                    return AlertePeremptionDto.builder()
                            .stockId(stock.getId())
                            .produitId(stock.getProduit().getId())
                            .nomProduit(stock.getProduit().getNom())
                            .quantite(stock.getQuantite())
                            .uniteBase(stock.getProduit().getUniteBase())
                            .dureeConservationJours(stock.getProduit().getDureeConservationJours())
                            .dernierEntreeDate(dernierEntreeDate)
                            .dateExpirationEstimee(dateExpiration)
                            .joursRestants(joursRestants)
                            .estExpire(joursRestants < 0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MouvementStockDto> getMouvements(UUID stockId) {
        findStockById(stockId);
        return mouvementStockRepository.findByStockIdOrderByDateDesc(stockId).stream()
                .map(this::toMouvementDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StockDto ajuster(UUID stockId, AjustementStockRequest request) {
        Stock stock = findStockById(stockId);

        validerSortie(stock, request.getType(), request.getQuantite());

        stock.appliquerMouvement(request.getType(), request.getQuantite());
        Stock saved = stockRepository.save(stock);

        enregistrerMouvement(saved, request.getType(), request.getQuantite(),
                request.getMotif(), request.getReference());

        return toDto(saved);
    }

    @Transactional
    public StockDto mettreAJourSeuil(UUID stockId, BigDecimal seuilAlerte) {
        if (seuilAlerte.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Le seuil d'alerte ne peut pas être négatif");
        }
        Stock stock = findStockById(stockId);
        stock.setSeuilAlerte(seuilAlerte);
        return toDto(stockRepository.save(stock));
    }

    // ─── Méthodes internes appelées par d'autres services ───────────────────────

    /**
     * Appelé par ProduitService à la création d'un produit.
     */
    @Transactional
    public void creerStockInitial(Produit produit) {
        if (stockRepository.existsByProduitId(produit.getId())) {
            return;
        }
        Stock stock = Stock.builder()
                .produit(produit)
                .quantite(BigDecimal.ZERO)
                .seuilAlerte(BigDecimal.ZERO)
                .build();
        stockRepository.save(stock);
    }

    /**
     * Appelé par VenteService (SORTIE/VENTE) et ProductionService (ENTREE/PRODUCTION).
     */
    @Transactional
    public void appliquerMouvement(UUID produitId, TypeMouvement type, BigDecimal quantite,
                                   MotifMouvement motif, String reference) {
        Stock stock = stockRepository.findByProduitId(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable pour le produit : " + produitId));

        validerSortie(stock, type, quantite);

        stock.appliquerMouvement(type, quantite);
        Stock saved = stockRepository.save(stock);

        enregistrerMouvement(saved, type, quantite, motif, reference);
    }

    // ─── Helpers privés ──────────────────────────────────────────────────────────

    private Stock findStockById(UUID id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock introuvable : " + id));
    }

    private void validerSortie(Stock stock, TypeMouvement type, BigDecimal quantite) {
        if (type == TypeMouvement.SORTIE && stock.getQuantite().compareTo(quantite) < 0) {
            throw new BusinessException(
                    String.format("Stock insuffisant : disponible %.3f, demandé %.3f",
                            stock.getQuantite(), quantite),
                    HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
    }

    private void enregistrerMouvement(Stock stock, TypeMouvement type, BigDecimal quantite,
                                      MotifMouvement motif, String reference) {
        MouvementStock mouvement = MouvementStock.builder()
                .stock(stock)
                .type(type)
                .quantite(quantite)
                .motif(motif)
                .reference(reference)
                .build();
        mouvementStockRepository.save(mouvement);
    }

    private StockDto toDto(Stock stock) {
        return StockDto.builder()
                .id(stock.getId())
                .produitId(stock.getProduit().getId())
                .nomProduit(stock.getProduit().getNom())
                .uniteBase(stock.getProduit().getUniteBase())
                .quantite(stock.getQuantite())
                .seuilAlerte(stock.getSeuilAlerte())
                .sousAlerte(stock.estSousAlerte())
                .updatedAt(stock.getUpdatedAt())
                .build();
    }

    private MouvementStockDto toMouvementDto(MouvementStock m) {
        return MouvementStockDto.builder()
                .id(m.getId())
                .stockId(m.getStock().getId())
                .type(m.getType())
                .quantite(m.getQuantite())
                .motif(m.getMotif())
                .reference(m.getReference())
                .date(m.getDate())
                .build();
    }
}
