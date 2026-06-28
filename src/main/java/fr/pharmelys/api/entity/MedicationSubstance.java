package fr.pharmelys.api.entity;

import jakarta.persistence.Entity;
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
public class MedicationSubstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Médicament concerné
     */
    @ManyToOne(optional = false)
    private Medication medication;

    /**
     * Substance active associée
     */
    @ManyToOne(optional = false)
    private Substance substance;

    private String pharmaceuticalForm;

    private String dosage;

    private String dosageReference;

    private String componentNature;
}
