package fr.pharmelys.api.exception.global;

/**
 * Levée quand les paramètres de recherche ne respectent pas les règles métier
 * (ex: terme trop court).
 */
public class InvalidSearchQueryException extends RuntimeException {
    public InvalidSearchQueryException(String message) {
        super(message);
    }
}
