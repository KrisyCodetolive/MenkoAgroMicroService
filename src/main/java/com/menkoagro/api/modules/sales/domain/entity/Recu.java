package com.menkoagro.api.modules.sales.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recu")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vente_id", nullable = false, unique = true)
    @JsonIgnore
    private Vente vente;

    @Column(name = "numero_recu", nullable = false, unique = true, length = 50)
    private String numeroRecu;

    @Column(name = "chemin_fichier", nullable = false, length = 500)
    private String cheminFichier;

    @Column(name = "genere_a", nullable = false)
    @Builder.Default
    private LocalDateTime genereA = LocalDateTime.now();
}
