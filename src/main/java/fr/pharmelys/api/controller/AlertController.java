package fr.pharmelys.api.controller;

import fr.pharmelys.api.dto.alert.AlertDTO;
import fr.pharmelys.api.dto.alert.CreateAlertRequestDTO;
import fr.pharmelys.api.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alertes de disponibilité", description = "Notification par email à la remise à disposition d'un médicament")
public class AlertController {

    private final AlertService alertService;

    @Operation(summary = "Créer une alerte de remise à disposition", description = "Si une alerte identique (même email + même médicament) est déjà active, "
            + "elle est retournée sans être dupliquée.")
    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody CreateAlertRequestDTO request) {
        AlertDTO alert = alertService.createAlert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }

    @Operation(summary = "Désactiver une alerte", description = "Soft delete : l'alerte passe à active=false mais n'est jamais supprimée physiquement.")
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Void> deactivateAlert(@PathVariable Long alertId) {
        alertService.deactivateAlert(alertId);
        return ResponseEntity.noContent().build();
    }
}