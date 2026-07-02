package fr.pharmelys.api.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.pharmelys.api.dto.medication.AlternativesResponseDTO;
import fr.pharmelys.api.dto.medication.MedicationDetailDTO;
import fr.pharmelys.api.dto.medication.MedicationSummaryDTO;

/**
 * Recherche, détail et calcul d'alternatives génériques pour les médicaments,
 * avec filtrage optionnel par allergies déclarées sur un profil patient.
 */
public interface MedicationService {

    /**
     * Recherche les médicaments par nom, avec pertinence et pagination.
     *
     * @param term     terme de recherche
     * @param pageable pagination et tri
     */
    Page<MedicationSummaryDTO> search(String term, Pageable pageable);

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