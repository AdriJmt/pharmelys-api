package fr.pharmelys.api.dto.medication;

/**
 * Référence légère vers le médicament source d'une recherche d'alternatives.
 *
 * @param cisCode        code CIS
 * @param name           dénomination
 * @param shortageStatus statut de rupture courant, {@code null} si disponible
 */
public record MedicationRefDTO(String cisCode, String name, String shortageStatus) {
}