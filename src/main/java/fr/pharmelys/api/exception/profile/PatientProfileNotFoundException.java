package fr.pharmelys.api.exception.profile;

import java.util.UUID;

/** Levée quand le profil patient référencé n'existe pas. */
public class PatientProfileNotFoundException extends RuntimeException {
    public PatientProfileNotFoundException(UUID profileId) {
        super("Patient profile not found: " + profileId);
    }
}
