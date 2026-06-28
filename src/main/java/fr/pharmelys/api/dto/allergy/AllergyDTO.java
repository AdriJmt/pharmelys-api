package fr.pharmelys.api.dto.allergy;

import fr.pharmelys.api.entity.AllergyType;

public record AllergyDTO(
                Long id,
                AllergyType type,
                String substanceCode,
                String substanceName,
                String freeTextLabel,
                String comment) {
}