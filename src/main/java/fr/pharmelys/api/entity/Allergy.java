package fr.pharmelys.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(indexes = {
        @Index(name = "idx_allergy_patient", columnList = "patient_profile_id"),
        @Index(name = "idx_allergy_substance", columnList = "substanceCode")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Allergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PatientProfile patientProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllergyType type;

    @Column(length = 50)
    private String substanceCode;

    @Column(length = 255)
    private String freeTextLabel;

    @Column(length = 500)
    private String comment;
}