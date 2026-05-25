package com.menkoagro.api.modules.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produit")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(columnDefinition = "TEXT" , nullable = true)
    private String description;

    @Column(name = "unite_base", nullable = false, length = 50)
    @Builder.Default
    private String uniteBase = "pièce";

    @Column(name = "est_perissable", nullable = false)
    @Builder.Default
    private boolean estPerissable = false;

    @Column(name = "duree_conservation_jours")
    private Integer dureeConservationJours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_produit_id", nullable = false)
    private CategorieProduit categorie;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Conditionnement> conditionnements = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
