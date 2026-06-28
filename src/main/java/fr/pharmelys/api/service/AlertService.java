package fr.pharmelys.api.service;

import fr.pharmelys.api.dto.alert.AlertDTO;
import fr.pharmelys.api.dto.alert.CreateAlertRequestDTO;

public interface AlertService {

    /**
     * Crée une alerte active, ou retourne l'alerte existante si une identique
     * (email+médicament) est déjà active.
     * 
     * @param request les informations de l'alerte à créer
     * @return l'alerte créée ou existante
     */
    AlertDTO createAlert(CreateAlertRequestDTO request);

    /**
     * Désactive l'alerte (soft delete) — ne supprime jamais la ligne, pour garder
     * un historique.
     * 
     * @param alertId l'identifiant de l'alerte à désactiver
     */
    void deactivateAlert(Long alertId);
}