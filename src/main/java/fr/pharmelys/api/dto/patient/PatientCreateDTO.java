package fr.pharmelys.api.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PatientCreateDTO(
    @NotBlank
    @Email
    String mail
) {}
