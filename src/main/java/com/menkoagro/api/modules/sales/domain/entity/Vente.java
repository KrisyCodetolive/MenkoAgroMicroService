package com.menkoagro.api.modules.sales.domain.entity;

import com.menkoagro.api.modules.customer.domain.entity.Client;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vente")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneVente> lignes = new ArrayList<>();

    @Column(name = "montant_total", nullable = false, precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal montantTotal = BigDecimal.ZERO;

    @Column(name = "date_vente", nullable = false)
    @Builder.Default
    private LocalDateTime dateVente = LocalDateTime.now();

    @OneToOne(mappedBy = "vente", cascade = CascadeType.ALL)
    private Recu recu;

    public BigDecimal calculerMontantTotal() {
        return lignes.stream()
                .map(LigneVente::getSousTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
