package fr.pharmelys.api.dto.profile;

import java.time.Instant;
import java.util.UUID;

public record ProfileDTO(UUID id, String email, Instant createdAt) {
}