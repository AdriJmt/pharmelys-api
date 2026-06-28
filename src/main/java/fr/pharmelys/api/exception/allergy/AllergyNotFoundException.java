package fr.pharmelys.api.exception.allergy;

public class AllergyNotFoundException extends RuntimeException {
    public AllergyNotFoundException(Long allergyId) {
        super("Allergy not found: " + allergyId);
    }
}
