package com.menkoagro.api.modules.stock.domain.entity;

import com.menkoagro.api.modules.product.domain.entity.Produit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stock")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false, unique = true)
    private Produit produit;

    @Column(nullable = false, precision = 15, scale = 3)
    @Builder.Default
    private BigDecimal quantite = BigDecimal.ZERO;

    @Column(name = "seuil_alerte", nullable = false, precision = 15, scale = 3)
    @Builder.Default
    private BigDecimal seuilAlerte = BigDecimal.ZERO;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public boolean estSousAlerte() {
        return quantite.compareTo(seuilAlerte) <= 0;
    }

    public void appliquerMouvement(TypeMouvement type, BigDecimal qte) {
        if (type == TypeMouvement.ENTREE) {
            this.quantite = this.quantite.add(qte);
        } else {
            this.quantite = this.quantite.subtract(qte);
        }
    }
}
