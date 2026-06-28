package fr.pharmelys.api.dto.allergy;

public record AllergyDTO(
        Long id,
        String type,
        String substanceCode,
        String freeTextLabel,
        String comment) {
}
