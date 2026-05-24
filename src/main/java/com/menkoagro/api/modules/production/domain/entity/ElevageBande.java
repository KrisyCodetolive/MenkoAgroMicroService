package com.menkoagro.api.modules.production.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("ELEVAGE")
@Getter @Setter
@NoArgsConstructor
public class ElevageBande extends Production {

    @Column(length = 50)
    private String reference;

    @Column(name = "nombre_animaux")
    private Integer nombreAnimaux;

    @Column(name = "type_animal", length = 100)
    private String typeAnimal;

    @Column(name = "taux_mortalite", precision = 5, scale = 2)
    private BigDecimal tauxMortalite;
}
