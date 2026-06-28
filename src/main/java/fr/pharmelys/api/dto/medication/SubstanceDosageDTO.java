package fr.pharmelys.api.dto.medication;

/**
 * Substance active d'un médicament, avec son dosage.
 *
 * @param substanceCode code de la substance dans le référentiel BDPM
 * @param name          dénomination de la substance (ex: "PARACETAMOL")
 * @param dosage        dosage tel que déclaré (ex: "1000 mg")
 */
public record SubstanceDosageDTO(String substanceCode, String name, String dosage) {
}