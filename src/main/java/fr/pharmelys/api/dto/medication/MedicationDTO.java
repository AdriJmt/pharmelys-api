package fr.pharmelys.api.dto.medication;

public record MedicationDTO(
        String cisCode,
        String name,
        String pharmaceuticalForm,
        String administrationRoute,
        String authorizationStatus,
        boolean enhancedSurveillance) {
}
