package fr.pharmelys.api.dto.alert;

import java.time.Instant;

public record AlertDTO(Long id, String email, String cisCode, boolean active, Instant createdAt) {
}
