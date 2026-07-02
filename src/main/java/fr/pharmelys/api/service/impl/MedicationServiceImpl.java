package fr.pharmelys.api.service.impl;

import java.text.Normalizer;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.pharmelys.api.dto.medication.AlternativeCandidateDTO;
import fr.pharmelys.api.dto.medication.AlternativesResponseDTO;
import fr.pharmelys.api.dto.medication.MedicationDetailDTO;
import fr.pharmelys.api.dto.medication.MedicationRefDTO;
import fr.pharmelys.api.dto.medication.MedicationSummaryDTO;
import fr.pharmelys.api.dto.medication.ShortageInfoDTO;
import fr.pharmelys.api.dto.medication.SubstanceDosageDTO;
import fr.pharmelys.api.entity.Allergy;
import fr.pharmelys.api.entity.AllergyType;
import fr.pharmelys.api.entity.Medication;
import fr.pharmelys.api.entity.MedicationSubstance;
import fr.pharmelys.api.entity.StockShortage;
import fr.pharmelys.api.entity.StockStatus;
import fr.pharmelys.api.exception.global.InvalidSearchQueryException;
import fr.pharmelys.api.exception.medication.MedicationNotFoundException;
import fr.pharmelys.api.exception.profile.PatientProfileNotFoundException;
import fr.pharmelys.api.repository.AllergyRepository;
import fr.pharmelys.api.repository.GenericGroupMemberRepository;
import fr.pharmelys.api.repository.MedicationRepository;
import fr.pharmelys.api.repository.MedicationSubstanceRepository;
import fr.pharmelys.api.repository.PatientProfileRepository;
import fr.pharmelys.api.repository.StockShortageRepository;
import fr.pharmelys.api.service.MedicationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {

        private static final String MEDICAL_DISCLAIMER = "Ces informations ne remplacent pas l'avis d'un médecin ou d'un pharmacien. "
                        + "Ne modifiez pas un traitement sans validation médicale.";

        private static final String ACTIVE_COMPONENT_NATURE = "SA";

        private final MedicationRepository medicationRepository;
        private final MedicationSubstanceRepository medicationSubstanceRepository;
        private final GenericGroupMemberRepository genericGroupMemberRepository;
        private final StockShortageRepository stockShortageRepository;
        private final AllergyRepository allergyRepository;
        private final PatientProfileRepository patientProfileRepository;

        private final Clock clock;

        @Override
        public Page<MedicationSummaryDTO> search(String term, Pageable pageable) {
                if (term == null || term.trim().length() < 3) {
                        throw new InvalidSearchQueryException("Le terme de recherche doit faire au moins 3 caractères");
                }

                // On ignore volontairement tout Sort venant du pageable : l'ordre de
                // pertinence est fixé dans la requête (cf. MedicationRepository).
                Pageable safePageable = PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize());

                // Note perf : une requete de statut de rupture par resultat (max page size).
                // Acceptable pour le volume actuel ; a regrouper en une seule requete batch
                // si la latence devient un probleme.
                return medicationRepository.searchByNameRelevance(term.trim(), safePageable)
                                .map(m -> new MedicationSummaryDTO(
                                                m.getCisCode(), m.getName(), m.getPharmaceuticalForm(),
                                                currentShortageStatus(m.getCisCode())));
        }

        @Override
        public MedicationDetailDTO getDetail(String cisCode) {
                Medication medication = medicationRepository.findById(cisCode)
                                .orElseThrow(() -> new MedicationNotFoundException(cisCode));

                List<SubstanceDosageDTO> activeSubstances = medicationSubstanceRepository
                                .findByMedication_CisCodeAndComponentNature(cisCode, ACTIVE_COMPONENT_NATURE)
                                .stream()
                                .map(ms -> new SubstanceDosageDTO(
                                                ms.getSubstance().getSubstanceCode(), ms.getSubstance().getName(),
                                                ms.getDosage()))
                                .toList();

                return new MedicationDetailDTO(
                                medication.getCisCode(),
                                medication.getName(),
                                medication.getPharmaceuticalForm(),
                                medication.getMarketingStatus(),
                                activeSubstances,
                                currentShortageInfo(cisCode));
        }

        @Override
        public AlternativesResponseDTO getAlternatives(String cisCode, UUID profileId) {
                Medication source = medicationRepository.findById(cisCode)
                                .orElseThrow(() -> new MedicationNotFoundException(cisCode));

                if (profileId != null && !patientProfileRepository.existsById(profileId)) {
                        throw new PatientProfileNotFoundException(profileId);
                }

                MedicationRefDTO sourceDTO = new MedicationRefDTO(
                                source.getCisCode(), source.getName(), currentShortageStatus(cisCode));

                List<String> groupIds = genericGroupMemberRepository.findByMedication_CisCode(cisCode).stream()
                                .map(member -> member.getGroup().getGroupId())
                                .distinct()
                                .toList();

                if (groupIds.isEmpty()) {
                        return new AlternativesResponseDTO(sourceDTO, profileId != null, List.of(), List.of(),
                                        "Pas de substitution générique connue pour ce médicament", MEDICAL_DISCLAIMER);
                }

                List<Medication> candidates = genericGroupMemberRepository
                                .findByGroup_GroupIdInAndMedication_CisCodeNot(groupIds, cisCode)
                                .stream()
                                .map(member -> member.getMedication())
                                .distinct()
                                .filter(this::isMarketed)
                                .filter(candidate -> !hasActiveShortage(candidate.getCisCode()))
                                .toList();

                List<Allergy> allergies = profileId != null
                                ? allergyRepository.findByPatientProfile_Id(profileId)
                                : List.of();

                List<AlternativeCandidateDTO> recommended = new ArrayList<>();
                List<AlternativeCandidateDTO> atRisk = new ArrayList<>();

                for (Medication candidate : candidates) {
                        List<MedicationSubstance> substances = medicationSubstanceRepository
                                        .findByMedication_CisCodeAndComponentNature(candidate.getCisCode(),
                                                        ACTIVE_COMPONENT_NATURE);
                        List<String> substanceNames = substances.stream().map(ms -> ms.getSubstance().getName())
                                        .toList();

                        String exclusionReason = profileId != null ? findExclusionReason(substances, allergies) : null;
                        AlternativeCandidateDTO dto = new AlternativeCandidateDTO(
                                        candidate.getCisCode(), candidate.getName(), substanceNames, exclusionReason);

                        (exclusionReason != null ? atRisk : recommended).add(dto);
                }

                return new AlternativesResponseDTO(sourceDTO, profileId != null, recommended, atRisk, null,
                                MEDICAL_DISCLAIMER);
        }

        // ------------------------------------------------------------------
        // Helpers
        // ------------------------------------------------------------------

        /**
         * Statut de rupture courant d'un médicament, ou {@code null} si disponible.
         * LIMITATION CONNUE : {@code StockShortage} ne porte pas encore de date de
         * remise à disposition,
         * donc une rupture resolue mais jamais re-importee avec un statut different
         * restera consideree active.
         */
        private ShortageInfoDTO currentShortageInfo(String cisCode) {
                return stockShortageRepository.findByMedication_CisCode(cisCode)
                                .filter(this::isActiveShortage)
                                .map(s -> new ShortageInfoDTO(s.getStatus().name(), s.getReportDate(),
                                                s.getRestockDate()))
                                .orElse(null);
        }

        private boolean isActiveShortage(StockShortage shortage) {
                return shortage.getStatus() == StockStatus.SHORTAGE
                                || shortage.getStatus() == StockStatus.TENSION;
        }

        private String currentShortageStatus(String cisCode) {
                ShortageInfoDTO info = currentShortageInfo(cisCode);
                return info != null ? info.status() : null;
        }

        @Override
        public boolean hasActiveShortage(String cisCode) {
                return currentShortageStatus(cisCode) != null;
        }

        private boolean isMarketed(Medication medication) {
                String status = medication.getMarketingStatus();
                return status != null && status.toLowerCase(Locale.ROOT).contains("commercialis");
        }

        /**
         * Cherche si une des substances actives d'un candidat correspond a une allergie
         * declaree.
         * Verifie d'abord les allergies de type ACTIVE_SUBSTANCE par code exact, puis
         * les allergies
         * EXCIPIENT/OTHER par correspondance texte heuristique (insensible
         * casse/accents).
         *
         * @return la raison d'exclusion a afficher, ou {@code null} si aucune
         *         correspondance
         */
        private String findExclusionReason(List<MedicationSubstance> substances, List<Allergy> allergies) {
                for (MedicationSubstance ms : substances) {
                        String substanceCode = ms.getSubstance().getSubstanceCode();
                        String substanceName = ms.getSubstance().getName();

                        boolean activeSubstanceMatch = allergies.stream()
                                        .filter(a -> a.getType() == AllergyType.ACTIVE_SUBSTANCE)
                                        .anyMatch(a -> substanceCode.equalsIgnoreCase(a.getSubstanceCode()));
                        if (activeSubstanceMatch) {
                                return "Contains " + substanceName + ", declared as an allergy";
                        }

                        Optional<Allergy> freeTextMatch = allergies.stream()
                                        .filter(a -> a.getType() == AllergyType.EXCIPIENT
                                                        || a.getType() == AllergyType.OTHER)
                                        .filter(a -> a.getFreeTextLabel() != null)
                                        .filter(a -> containsIgnoreCaseAndAccents(substanceName, a.getFreeTextLabel()))
                                        .findFirst();
                        if (freeTextMatch.isPresent()) {
                                return "May contain " + freeTextMatch.get().getFreeTextLabel()
                                                + " (declared allergy, heuristic text match)";
                        }
                }
                return null;
        }

        private boolean containsIgnoreCaseAndAccents(String haystack, String needle) {
                if (haystack == null || needle == null) {
                        return false;
                }
                return normalize(haystack).contains(normalize(needle));
        }

        private String normalize(String value) {
                return Normalizer.normalize(value, Normalizer.Form.NFD)
                                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                                .toLowerCase(Locale.ROOT);
        }
}