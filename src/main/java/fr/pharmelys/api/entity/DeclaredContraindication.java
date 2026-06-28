package fr.pharmelys.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Cette entité est purement déclarative et informative. Aucune logique de
 * filtrage automatique ne doit être codée dessus dans cette première version
 * (les données
 * structurées disponibles ne le permettent pas de façon fiable). Elle sert
 * uniquement à afficher un encart d'avertissement à l'utilisateur ("vous avez
 * déclaré : grossesse — vérifiez cette information avec votre médecin avant de
 * prendre toute alternative").
 */
@Entity
@Table(
    indexes = {
        @Index(name = "idx_contraindication_patient", columnList = "patient_id"),
        @Index(name = "idx_contraindication_label", columnList = "label")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeclaredContraindication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Patient concerné
     */
    @ManyToOne(optional = false)
    private PatientProfile patient;

    /**
     * Libellé libre ou tag prédéfini (ex: grossesse, insuffisance rénale)
     */
    @Column(nullable = false, length = 255)
    private String label;

    /**
     * Commentaire additionnel
     */
    @Column(length = 500)
    private String comment;
}
