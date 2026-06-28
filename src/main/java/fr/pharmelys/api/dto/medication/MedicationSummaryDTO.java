package fr.pharmelys.api.dto.medication;

/**
 * Résultat compact d'une recherche de médicament.
 *
 * @param cisCode            code CIS du médicament
 * @param name               dénomination
 * @param pharmaceuticalForm forme pharmaceutique (ex: "comprimé")
 * @param shortageStatus     statut de rupture courant ({@code SHORTAGE},
 *                           {@code TENSION}...),
 *                           {@code null} si le médicament est disponible
 */
public record MedicationSummaryDTO(String cisCode, String name, String pharmaceuticalForm, String shortageStatus) {
}