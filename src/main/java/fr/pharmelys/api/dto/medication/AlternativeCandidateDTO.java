package fr.pharmelys.api.dto.medication;

import java.util.List;

/**
 * Médicament alternatif candidat.
 *
 * @param cisCode          code CIS
 * @param name             dénomination
 * @param activeSubstances noms des substances actives (vue simplifiée pour
 *                         l'affichage)
 * @param exclusionReason  raison pour laquelle ce candidat est classé "à
 *                         risque"
 *                         (correspondance avec une allergie déclarée),
 *                         {@code null} si recommandé
 */
public record AlternativeCandidateDTO(
        String cisCode,
        String name,
        List<String> activeSubstances,
        String exclusionReason) {
}