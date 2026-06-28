package fr.pharmelys.api.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_medication_cis", columnNames = "cisCode")
}, indexes = {
        @Index(name = "idx_medication_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Medication {

    /**
     * Code CIS (8 caractères max)
     */
    @Id
    @Column(name = "cis_code",length = 8, nullable = false, updatable = false)
    private String cisCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String pharmaceuticalForm;

    @Column(length = 255)
    private String administrationRoute;

    @Column(length = 100)
    private String authorizationStatus;

    @Column(length = 100)
    private String authorizationProcedure;

    @Column(length = 100)
    private String marketingStatus;

    private LocalDate authorizationDate;

    @Column(length = 255)
    private String holders;

    /**
     * Médicament sous surveillance renforcée ANSM
     */
    private boolean enhancedSurveillance;
}
