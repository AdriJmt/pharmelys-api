package fr.pharmelys.api.dto.profile;

import fr.pharmelys.api.entity.AllergyType;
import jakarta.validation.constraints.NotNull;

/**
 * @param type          type d'allergie déclarée
 * @param substanceCode code substance (référentiel BDPM), pertinent si type =
 *                      ACTIVE_SUBSTANCE
 * @param freeTextLabel saisie libre (excipient, autre, ou substance non
 *                      référencée)
 * @param comment       commentaire libre optionnel
 */
public record CreateAllergyRequestDTO(
        @NotNull AllergyType type,
        String substanceCode,
        String freeTextLabel,
        String comment) {
}