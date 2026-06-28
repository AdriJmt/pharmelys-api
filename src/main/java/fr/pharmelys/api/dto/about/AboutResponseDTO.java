package fr.pharmelys.api.dto.about;

public record AboutResponseDTO(
        String dataSource,
        String lastImportUpdate,
        String medicalDeviceDisclaimer,
        String licenseUrl) {
}