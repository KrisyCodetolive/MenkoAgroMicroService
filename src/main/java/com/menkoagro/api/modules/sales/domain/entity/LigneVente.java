package com.menkoagro.api.modules.sales.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.menkoagro.api.modules.product.domain.entity.Conditionnement;
import com.menkoagro.api.modules.product.domain.entity.Produit;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_vente")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneVente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false)
    @JsonIgnore
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conditionnement_id", nullable = false)
    private Conditionnement conditionnement;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantite;

    @Column(name = "prix_unitaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "sous_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal sousTotal;

    @Column(name = "qte_deduit_stock", nullable = false, precision = 15, scale = 3)
    private BigDecimal qteDeduitStock;
}
