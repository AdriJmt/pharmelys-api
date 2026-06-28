package fr.pharmelys.api.dto.contraindication;

import jakarta.validation.constraints.NotBlank;

public record CreateContraindicationRequestDTO(@NotBlank String label, String comment) {
}