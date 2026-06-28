package fr.pharmelys.api.service;

import java.util.List;
import java.util.UUID;

import fr.pharmelys.api.dto.medication.AlternativesResponseDTO;
import fr.pharmelys.api.dto.medication.MedicationDetailDTO;
import fr.pharmelys.api.dto.medication.MedicationSummaryDTO;

/**
 * Recherche, détail et calcul d'alternatives génériques pour les médicaments,
 * avec filtrage optionnel par allergies déclarées sur un profil patient.
 */
public interface MedicationService {

    /**
     * Recherche des médicaments par nom, insensible à la casse et aux accents.
     *
     * @param term terme recherché, doit faire au moins 3 caractères
     * @return jusqu'à 20 résultats triés par pertinence
     * @throws fr.pharmelys.api.exception.InvalidSearchQueryException si
     *                                                                {@code term}
     *                                                                fait moins de
     *                                                                3 caractères
     */
    List<MedicationSummaryDTO> search(String term);

    /**
     * Récupère le détail complet d'un médicament.
     *
     * @param cisCode code CIS du médicament
     * @throws fr.pharmelys.api.exception.MedicationNotFoundException si aucun
     *                                                                médicament ne
     *                                                                correspond
     */
    MedicationDetailDTO getDetail(String cisCode);

    /**
     * Calcule les alternatives génériques disponibles pour un médicament, en
     * excluant celles
     * actuellement en rupture/tension et celles non commercialisées. Si un
     * {@code profileId}
     * est fourni, sépare les résultats entre {@code recommended} et {@code atRisk}
     * selon les
     * allergies déclarées sur ce profil (substances actives en correspondance
     * exacte par code,
     * excipients/autres en correspondance texte heuristique).
     *
     * @param cisCode   code CIS du médicament source
     * @param profileId identifiant du profil patient, optionnel ({@code null} = pas
     *                  de filtrage allergies)
     * @throws fr.pharmelys.api.exception.MedicationNotFoundException     si le
     *                                                                    médicament
     *                                                                    source
     *                                                                    n'existe
     *                                                                    pas
     * @throws fr.pharmelys.api.exception.PatientProfileNotFoundException si
     *                                                                    {@code profileId}
     *                                                                    est fourni
     *                                                                    mais
     *                                                                    introuvable
     */
    AlternativesResponseDTO getAlternatives(String cisCode, UUID profileId);

    /**
     * Vérifie si un médicament est actuellement en rupture ou tension de stock.
     *
     * @param cisCode code CIS du médicament
     * @return {@code true} si le médicament est en rupture/tension, {@code false}
     *         sinon
     */
    boolean hasActiveShortage(String cisCode);
}