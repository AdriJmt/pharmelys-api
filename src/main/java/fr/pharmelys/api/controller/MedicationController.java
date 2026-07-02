package fr.pharmelys.api.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.pharmelys.api.dto.ErrorResponseDTO;
import fr.pharmelys.api.dto.medication.AlternativesResponseDTO;
import fr.pharmelys.api.dto.medication.MedicationDetailDTO;
import fr.pharmelys.api.dto.medication.MedicationSummaryDTO;
import fr.pharmelys.api.dto.shortage.ShortageStatsDTO;
import fr.pharmelys.api.service.MedicationService;
import fr.pharmelys.api.service.ShortageStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Recherche de médicaments, consultation de détail, et calcul d'alternatives
 * génériques
 * filtrées par allergies déclarées.
 */
@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Tag(name = "Médicaments", description = "Recherche, détail et alternatives génériques de médicaments")
public class MedicationController {

        private final MedicationService medicationService;
        private final ShortageStatsService shortageStatsService;

        @Operation(summary = "Rechercher un médicament par nom", description = "Recherche insensible à la casse et aux accents sur le nom du médicament. "
                        + "Résultats triés par pertinence (correspondance exacte du début de chaîne en premier), "
                        + "paginés, avec leur statut de rupture courant.")
        @ApiResponse(responseCode = "200", description = "Page de médicaments correspondants (peut être vide)")
        @ApiResponse(responseCode = "400", description = "Terme de recherche trop court (moins de 3 caractères)", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        @GetMapping("/search")
        public ResponseEntity<Page<MedicationSummaryDTO>> search(
                        @Parameter(description = "Terme recherché, au moins 3 caractères", example = "doliprane") @RequestParam String q,
                        @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
                return ResponseEntity.ok(medicationService.search(q, pageable));
        }

        @Operation(summary = "Détail d'un médicament", description = "Retourne le nom, la forme pharmaceutique, le statut de commercialisation, "
                        + "les substances actives avec dosage, et le détail de la rupture en cours "
                        + "(statut, date de signalement, date de remise à disposition prévue) s'il y en a une.")
        @GetMapping("/{cisCode}")
        public ResponseEntity<MedicationDetailDTO> getDetail(
                        @Parameter(description = "Code CIS du médicament", example = "60234152") @PathVariable String cisCode) {
                return ResponseEntity.ok(medicationService.getDetail(cisCode));
        }

        @Operation(summary = "Alternatives génériques d'un médicament, filtrées par allergies", description = "Retourne les médicaments du même groupe générique actuellement disponibles "
                        + "(non en rupture, commercialisés), séparés en deux listes si un profil patient est "
                        + "fourni : 'recommended' (aucune correspondance d'allergie) et 'atRisk' (correspondance "
                        + "trouvée, avec raison). Sans profileId, allergiesChecked vaut false et tout est dans "
                        + "'recommended'. Le champ disclaimer rappelle que ces informations ne remplacent pas "
                        + "l'avis d'un professionnel de santé.")
        @ApiResponse(responseCode = "200", description = "Alternatives calculées (listes éventuellement vides)")
        @ApiResponse(responseCode = "404", description = "Médicament ou profil patient introuvable", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
        @GetMapping("/{cisCode}/alternatives")
        public ResponseEntity<AlternativesResponseDTO> getAlternatives(
                        @Parameter(description = "Code CIS du médicament source", example = "60234152") @PathVariable String cisCode,
                        @Parameter(description = "Identifiant du profil patient pour filtrer par allergies (optionnel)") @RequestParam(required = false) UUID profileId) {
                return ResponseEntity.ok(medicationService.getAlternatives(cisCode, profileId));
        }

        @Operation(summary = "Statistiques globales des ruptures et tensions en cours", description = "Retourne le nombre de médicaments actuellement en rupture de stock et en tension "
                        + "d'approvisionnement, tous médicaments confondus.")
        @ApiResponse(responseCode = "200", description = "Statistiques calculées")
        @GetMapping("/shortages/stats")
        public ResponseEntity<ShortageStatsDTO> getShortageStats() {
                return ResponseEntity.ok(shortageStatsService.getStats());
        }
}