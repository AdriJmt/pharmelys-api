package fr.pharmelys.api.dto.alert;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAlertRequestDTO(@NotBlank @Email String email, @NotBlank String cisCode) {
}
