package fr.pharmelys.api.dto.medication;

import java.util.List;

/**
 * Réponse complète de l'endpoint alternatives.
 *
 * @param sourceMedication médicament d'origine
 * @param allergiesChecked {@code true} si un profil a été fourni et que les
 *                         allergies ont été vérifiées
 * @param recommended      alternatives sans correspondance d'allergie connue
 * @param atRisk           alternatives à risque (correspondance d'allergie
 *                         trouvée), avec raison
 * @param message          message informatif optionnel (ex: absence de groupe
 *                         générique connu), {@code null} sinon
 * @param disclaimer       rappel fixe : cette information ne remplace pas
 *                         l'avis d'un professionnel de santé
 */
public record AlternativesResponseDTO(
        MedicationRefDTO sourceMedication,
        boolean allergiesChecked,
        List<AlternativeCandidateDTO> recommended,
        List<AlternativeCandidateDTO> atRisk,
        String message,
        String disclaimer) {
}