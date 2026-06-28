package fr.pharmelys.api.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.pharmelys.api.dto.allergy.AllergyDTO;
import fr.pharmelys.api.dto.contraindication.ContraindicationDTO;
import fr.pharmelys.api.dto.contraindication.CreateContraindicationRequestDTO;
import fr.pharmelys.api.dto.profile.CreateAllergyRequestDTO;
import fr.pharmelys.api.dto.profile.CreateProfileRequestDTO;
import fr.pharmelys.api.dto.profile.ProfileDTO;
import fr.pharmelys.api.entity.Allergy;
import fr.pharmelys.api.entity.DeclaredContraindication;
import fr.pharmelys.api.entity.PatientProfile;
import fr.pharmelys.api.exception.allergy.AllergyNotFoundException;
import fr.pharmelys.api.exception.profile.PatientProfileNotFoundException;
import fr.pharmelys.api.mapper.AllergyMapper;
import fr.pharmelys.api.mapper.ContraindicationMapper;
import fr.pharmelys.api.mapper.ProfileMapper;
import fr.pharmelys.api.repository.AllergyRepository;
import fr.pharmelys.api.repository.DeclaredContraindicationRepository;
import fr.pharmelys.api.repository.PatientProfileRepository;
import fr.pharmelys.api.repository.SubstanceRepository;
import fr.pharmelys.api.service.ProfileService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final PatientProfileRepository patientProfileRepository;
    private final AllergyRepository allergyRepository;
    private final DeclaredContraindicationRepository contraindicationRepository;
    private final SubstanceRepository substanceRepository;

    private final ProfileMapper profileMapper;
    private final AllergyMapper allergyMapper;
    private final ContraindicationMapper contraindicationMapper;

    @Override
    @Transactional
    public ProfileDTO createProfile(CreateProfileRequestDTO request) {
        PatientProfile profile = new PatientProfile();
        profile.setEmail(request != null ? request.email() : null);
        PatientProfile saved = patientProfileRepository.save(profile);
        return profileMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteProfile(UUID profileId) {
        PatientProfile profile = findProfileOrThrow(profileId);
        // Suppression via l'entité (pas deleteById) pour garantir le declenchement du
        // cascade = ALL / orphanRemoval defini sur PatientProfile.allergies et
        // .contraindications.
        patientProfileRepository.delete(profile);
    }

    @Override
    @Transactional
    public AllergyDTO addAllergy(UUID profileId, CreateAllergyRequestDTO request) {
        PatientProfile profile = findProfileOrThrow(profileId);

        Allergy allergy = new Allergy();
        allergy.setPatientProfile(profile);
        allergy.setType(request.type());
        allergy.setComment(request.comment());

        // Regle metier : on ne rejette jamais si le substanceCode est inconnu du
        // referentiel,
        // on degrade simplement vers un matching texte (freeTextLabel) cote service
        // alternatives.
        String resolvedSubstanceCode = null;
        if (request.substanceCode() != null && substanceRepository.existsById(request.substanceCode())) {
            resolvedSubstanceCode = request.substanceCode();
        }
        allergy.setSubstanceCode(resolvedSubstanceCode);
        allergy.setFreeTextLabel(request.freeTextLabel());

        Allergy saved = allergyRepository.save(allergy);
        return allergyMapper.toDto(saved);
    }

    @Override
    public List<AllergyDTO> listAllergies(UUID profileId) {
        findProfileOrThrow(profileId); // 404 explicite si le profil n'existe pas
        return allergyRepository.findByPatientProfile_Id(profileId).stream().map(allergyMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteAllergy(UUID profileId, Long allergyId) {
        findProfileOrThrow(profileId);
        Allergy allergy = allergyRepository.findById(allergyId)
                .filter(a -> a.getPatientProfile().getId().equals(profileId))
                .orElseThrow(() -> new AllergyNotFoundException(allergyId));
        allergyRepository.delete(allergy);
    }

    @Override
    @Transactional
    public ContraindicationDTO addContraindication(UUID profileId, CreateContraindicationRequestDTO request) {
        PatientProfile profile = findProfileOrThrow(profileId);

        DeclaredContraindication contraindication = new DeclaredContraindication();
        contraindication.setPatient(profile);
        contraindication.setLabel(request.label());
        contraindication.setComment(request.comment());

        DeclaredContraindication saved = contraindicationRepository.save(contraindication);
        return contraindicationMapper.toDto(saved);
    }

    @Override
    public List<ContraindicationDTO> listContraindications(UUID profileId) {
        findProfileOrThrow(profileId);
        return contraindicationRepository.findByPatient_Id(profileId)
                .stream()
                .map(c -> new ContraindicationDTO(c.getId(), c.getLabel(), c.getComment()))
                .toList();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private PatientProfile findProfileOrThrow(UUID profileId) {
        return patientProfileRepository.findById(profileId)
                .orElseThrow(() -> new PatientProfileNotFoundException(profileId));
    }

}