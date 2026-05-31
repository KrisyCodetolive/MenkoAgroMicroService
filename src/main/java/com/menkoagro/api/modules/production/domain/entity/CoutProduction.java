package com.menkoagro.api.modules.production.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cout_production")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoutProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", nullable = false)
    @JsonIgnore
    private Production production;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etape_id", nullable = true)
    private EtapeProduction etape;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CategorieCout categorie;

    @Column(nullable = false, length = 200)
    private String libelle;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    private LocalDate date;
}
