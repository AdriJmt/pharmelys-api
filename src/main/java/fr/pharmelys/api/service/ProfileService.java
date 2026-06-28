package fr.pharmelys.api.service;

import fr.pharmelys.api.dto.allergy.AllergyDTO;
import fr.pharmelys.api.dto.contraindication.*;
import fr.pharmelys.api.dto.profile.*;
import java.util.List;
import java.util.UUID;

public interface ProfileService {

    ProfileDTO createProfile(CreateProfileRequestDTO request);

    /**
     * Supprime le profil et toutes ses données associées (allergies,
     * contre-indications) en cascade.
     */
    void deleteProfile(UUID profileId);

    /**
     * Ajoute une allergie au profil patient.
     *
     * @param profileId l'identifiant du profil patient
     * @param request   les informations de l'allergie à ajouter
     * @return l'allergie ajoutée
     */
    AllergyDTO addAllergy(UUID profileId, CreateAllergyRequestDTO request);

    /**
     * Liste les allergies du profil patient.
     *
     * @param profileId l'identifiant du profil patient
     * @return la liste des allergies
     */
    List<AllergyDTO> listAllergies(UUID profileId);

    /**
     * Supprime une allergie du profil patient.
     *
     * @param profileId l'identifiant du profil patient
     * @param allergyId l'identifiant de l'allergie à supprimer
     */
    void deleteAllergy(UUID profileId, Long allergyId);

    /**
     * Ajoute une contre-indication au profil patient.
     *
     * @param profileId l'identifiant du profil patient
     * @param request   les informations de la contre-indication à ajouter
     * @return la contre-indication ajoutée
     */
    ContraindicationDTO addContraindication(UUID profileId, CreateContraindicationRequestDTO request);

    /**
     * Liste les contre-indications du profil patient.
     *
     * @param profileId l'identifiant du profil patient
     * @return la liste des contre-indications
     */
    List<ContraindicationDTO> listContraindications(UUID profileId);
}
