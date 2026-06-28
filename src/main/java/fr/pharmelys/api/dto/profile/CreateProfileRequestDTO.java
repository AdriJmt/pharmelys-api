package fr.pharmelys.api.dto.profile;

import jakarta.validation.constraints.Email;

/**
 * Corps optionnel pour la création d'un profil. Un profil peut être totalement
 * anonyme.
 */
public record CreateProfileRequestDTO(@Email String email) {
}