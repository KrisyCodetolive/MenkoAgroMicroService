package com.menkoagro.api.modules.production.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "etape_production")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtapeProduction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_id", nullable = false)
    @JsonIgnore
    private ProductionAgricole production;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TypeEtape type;

    @Column(name = "date_realisation", nullable = false)
    private LocalDate dateRealisation;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
