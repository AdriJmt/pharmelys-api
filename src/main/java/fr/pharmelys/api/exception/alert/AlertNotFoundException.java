package fr.pharmelys.api.exception.alert;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(Long alertId) {
        super("Alert subscription not found: " + alertId);
    }
}