package fr.pharmelys.api.entity;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockShortage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Médicament stocké
     */
    @ManyToOne(optional = false)
    private Medication medication;

    /**
     * Nom brut importé (ex: fichier fournisseur)
     */
    @Column(length = 255)
    private String rawName;

    @Enumerated(EnumType.STRING)
    private StockStatus status;

    private LocalDate reportDate;

    @Column(nullable = true)
    private LocalDate restockDate;

    @UpdateTimestamp
    private Instant lastUpdatedAt;
}