package fr.pharmelys.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fr.pharmelys.api.dto.ErrorResponseDTO;
import fr.pharmelys.api.exception.alert.AlertNotFoundException;
import fr.pharmelys.api.exception.allergy.AllergyNotFoundException;
import fr.pharmelys.api.exception.global.InvalidSearchQueryException;
import fr.pharmelys.api.exception.medication.MedicationNotFoundException;
import fr.pharmelys.api.exception.profile.PatientProfileNotFoundException;

/**
 * Traduit les exceptions métier en réponses HTTP cohérentes pour toute l'API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MedicationNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleMedicationNotFound(MedicationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(PatientProfileNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProfileNotFound(PatientProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(InvalidSearchQueryException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidQuery(InvalidSearchQueryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(AllergyNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAllergyNotFound(AllergyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(AlertNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlertNotFound(AlertNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(ex.getMessage()));
    }
}