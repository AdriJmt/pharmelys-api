package fr.pharmelys.api.exception.medication;

/** Levée quand aucun médicament ne correspond au code CIS demandé. */
public class MedicationNotFoundException extends RuntimeException {
    public MedicationNotFoundException(String cisCode) {
        super("Medication not found for CIS code: " + cisCode);
    }
}