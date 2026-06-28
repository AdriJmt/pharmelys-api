package fr.pharmelys.api.dto.patient;

import java.time.Instant;
import java.util.UUID;

public record PatientDTO(
    UUID id,
    String mail,
    Instant createdAt
) {}
