package fr.pharmelys.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.pharmelys.api.dto.allergy.AllergyDTO;
import fr.pharmelys.api.dto.contraindication.ContraindicationDTO;
import fr.pharmelys.api.dto.contraindication.CreateContraindicationRequestDTO;
import fr.pharmelys.api.dto.profile.CreateAllergyRequestDTO;
import fr.pharmelys.api.dto.profile.CreateProfileRequestDTO;
import fr.pharmelys.api.dto.profile.ProfileDTO;
import fr.pharmelys.api.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Gestion des profils patients (anonymes par defaut), de leurs allergies et
 * contre-indications
 * declarees. Aucun endpoint de listing global n'existe volontairement (cf RGPD
 * - donnees de sante).
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profils patients", description = "Profils anonymes, allergies et contre-indications declaratives")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "Créer un profil patient", description = "Crée un profil anonyme par défaut (email optionnel). "
            + "Le profil est identifié uniquement par son UUID, à conserver côté client.")
    @PostMapping
    public ResponseEntity<ProfileDTO> createProfile(
            @RequestBody(required = false) CreateProfileRequestDTO request) {
        ProfileDTO created = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Supprimer un profil (droit à l'effacement RGPD)", description = "Supprime le profil ainsi que toutes ses allergies et contre-indications en cascade.")
    @DeleteMapping("/{profileId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable UUID profileId) {
        profileService.deleteProfile(profileId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ajouter une allergie déclarée", description = "Si substanceCode n'est pas reconnu dans le référentiel, la requête n'est pas "
            + "rejetée : l'allergie est stockée via freeTextLabel et le matching se fera en texte.")
    @PostMapping("/{profileId}/allergies")
    public ResponseEntity<AllergyDTO> addAllergy(
            @PathVariable UUID profileId,
            @Valid @RequestBody CreateAllergyRequestDTO request) {
        AllergyDTO created = profileService.addAllergy(profileId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Lister les allergies d'un profil")
    @GetMapping("/{profileId}/allergies")
    public ResponseEntity<List<AllergyDTO>> listAllergies(@PathVariable UUID profileId) {
        return ResponseEntity.ok(profileService.listAllergies(profileId));
    }

    @Operation(summary = "Supprimer une allergie")
    @DeleteMapping("/{profileId}/allergies/{allergyId}")
    public ResponseEntity<Void> deleteAllergy(
            @PathVariable UUID profileId,
            @Parameter(description = "Identifiant de l'allergie") @PathVariable Long allergyId) {
        profileService.deleteAllergy(profileId, allergyId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ajouter une contre-indication déclarée", description = "Purement informatif : aucune verification clinique automatique n'est effectuee. "
            + "Sert uniquement a afficher un avertissement a l'utilisateur.")
    @PostMapping("/{profileId}/contraindications")
    public ResponseEntity<ContraindicationDTO> addContraindication(
            @PathVariable UUID profileId,
            @Valid @RequestBody CreateContraindicationRequestDTO request) {
        ContraindicationDTO created = profileService.addContraindication(profileId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Lister les contre-indications déclarées d'un profil")
    @GetMapping("/{profileId}/contraindications")
    public ResponseEntity<List<ContraindicationDTO>> listContraindications(@PathVariable UUID profileId) {
        return ResponseEntity.ok(profileService.listContraindications(profileId));
    }
}