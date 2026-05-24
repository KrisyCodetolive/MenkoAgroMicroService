package com.menkoagro.api.modules.production.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("AGRICOLE")
@Getter @Setter
@NoArgsConstructor
public class ProductionAgricole extends Production {

    @Column(length = 100)
    private String zone;

    @Column(name = "superficie_parcelle", precision = 10, scale = 2)
    private BigDecimal superficieParcelle;

    @OneToMany(mappedBy = "production", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EtapeProduction> etapes = new ArrayList<>();
}
