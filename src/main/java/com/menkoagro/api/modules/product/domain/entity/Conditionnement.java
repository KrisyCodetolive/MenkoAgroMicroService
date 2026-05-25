package com.menkoagro.api.modules.product.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "conditionnement")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conditionnement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    @JsonIgnore
    private Produit produit;

    @Column(nullable = false, length = 100)
    private String libelle;

    @Column(name = "quantite_base", nullable = false, precision = 10, scale = 3)
    @Builder.Default
    private BigDecimal quantiteBase = BigDecimal.ONE;

    @Column(name = "prix_vente", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixVente;

    @Column(name = "est_par_defaut", nullable = false)
    @Builder.Default
    private boolean estParDefaut = false;
}
