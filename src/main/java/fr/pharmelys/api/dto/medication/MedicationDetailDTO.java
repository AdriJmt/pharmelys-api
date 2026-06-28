package fr.pharmelys.api.dto.medication;

import java.util.List;

/**
 * Détail complet d'un médicament.
 *
 * @param cisCode            code CIS
 * @param name               dénomination
 * @param pharmaceuticalForm forme pharmaceutique
 * @param marketingStatus    statut de commercialisation (ex: "Commercialisée")
 * @param activeSubstances   substances actives avec dosage
 * @param shortage           détail de la rupture courante, {@code null} si
 *                           disponible
 */
public record MedicationDetailDTO(
        String cisCode,
        String name,
        String pharmaceuticalForm,
        String marketingStatus,
        List<SubstanceDosageDTO> activeSubstances,
        ShortageInfoDTO shortage) {
}