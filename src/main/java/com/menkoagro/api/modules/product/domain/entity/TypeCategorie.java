package com.menkoagro.api.modules.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "type_categorie")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeCategorie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;
}
